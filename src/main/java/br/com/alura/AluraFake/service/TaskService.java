package br.com.alura.AluraFake.service;

import br.com.alura.AluraFake.domain.course.Course;
import br.com.alura.AluraFake.repository.CourseRepository;
import br.com.alura.AluraFake.domain.course.Status;
import br.com.alura.AluraFake.exception.ApiException;
import br.com.alura.AluraFake.domain.task.Option;
import br.com.alura.AluraFake.dto.request.task.OptionDTO;
import br.com.alura.AluraFake.domain.task.Task;
import br.com.alura.AluraFake.dto.request.task.TaskDTO;
import br.com.alura.AluraFake.repository.TaskRepository;
import br.com.alura.AluraFake.domain.task.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class TaskService {

    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(CourseRepository courseRepository,TaskRepository taskRepository){
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task createOpenTextTask(TaskDTO taskDTO) {
        Course course = validateCourse(taskDTO.getCourseId());
        validateStatement(taskDTO.getStatement(), taskDTO.getCourseId());
        validateOrder(taskDTO.getOrder(), taskDTO.getCourseId());

        Task task = new Task();
        task.setType(Type.OPEN_TEXT);
        task.setStatement(taskDTO.getStatement());
        task.setOrder(taskDTO.getOrder());
        task.setCourse(course);

        adjustTaskOrdersForInsertion(taskDTO.getCourseId(), taskDTO.getOrder());

        return taskRepository.save(task);
    }

    @Transactional
    public Task createSingleChoiceTask(TaskDTO taskDTO) {
        Course course = validateCourse(taskDTO.getCourseId());
        validateStatement(taskDTO.getStatement(), taskDTO.getCourseId());
        validateOrder(taskDTO.getOrder(), taskDTO.getCourseId());

        validateSingleChoiceOptions(taskDTO.getOptions(), taskDTO.getStatement());

        List<Option> options = convertToOptions(taskDTO.getOptions());

        Task task = new Task();
        task.setType(Type.SINGLE_CHOICE);
        task.setStatement(taskDTO.getStatement());
        task.setOrder(taskDTO.getOrder());
        task.setCourse(course);

        if (options != null) {
            task.setOptions(options);
            options.forEach(option -> option.setTask(task));
        }

        adjustTaskOrdersForInsertion(taskDTO.getCourseId(), taskDTO.getOrder());

        return taskRepository.save(task);
    }

    @Transactional
    public Task createMultipleChoiceTask(TaskDTO taskDTO) {
        Course course = validateCourse(taskDTO.getCourseId());
        validateStatement(taskDTO.getStatement(), taskDTO.getCourseId());
        validateOrder(taskDTO.getOrder(), taskDTO.getCourseId());
        validateMultipleChoiceOptions(taskDTO.getOptions(), taskDTO.getStatement());

        List<Option> options = convertToOptions(taskDTO.getOptions());

        Task task = new Task();
        task.setType(Type.MULTIPLE_CHOICE);
        task.setStatement(taskDTO.getStatement());
        task.setOrder(taskDTO.getOrder());
        task.setCourse(course);

        if (options != null) {
            task.setOptions(options);
            options.forEach(option -> option.setTask(task));
        }

        adjustTaskOrdersForInsertion(taskDTO.getCourseId(), taskDTO.getOrder());

        return taskRepository.save(task);
    }



    private Course validateCourse(Long id){
        return courseRepository.findById(id)
                .filter(c -> c.getStatus() == Status.BUILDING)
                .orElseThrow(() -> new ApiException("Curso não encontrado ou não está no status BUILDING", BAD_REQUEST));

    }

    private void validateStatement(String statement, Long idCourse) {

        List<Task> tasks = taskRepository.findByCourseId(idCourse);
        if (tasks != null && !tasks.isEmpty() && tasks.stream()
                .anyMatch(a -> a.getStatement().equalsIgnoreCase(statement))) {
            throw new  ApiException("Já existe uma atividade com esse enunciado no curso", BAD_REQUEST);
        }
    }

    private void validateOrder(Integer order, Long courseId) {

        Integer maxOrder = taskRepository.findMaxOrderByCourseId(courseId).orElse(0);
        if (order > maxOrder + 1) {
            throw new ApiException("A ordem da nova atividade deve seguir a sequência existente.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateSingleChoiceOptions(List<OptionDTO> options, String statement) {
        if (options == null || options.size() < 2 || options.size() > 5) {
            throw new ApiException("Deve haver entre 2 e 5 opções", HttpStatus.BAD_REQUEST);
        }

        long correctCount = options.stream()
                .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                .count();

        if (correctCount != 1) {
            throw new ApiException("Deve haver exatamente uma opção correta", HttpStatus.BAD_REQUEST);
        }

        validateCommonOptions(options, statement);
    }

    private void validateMultipleChoiceOptions(List<OptionDTO> options, String statement) {
        if (options == null || options.size() < 3 || options.size() > 5) {
            throw new ApiException("Deve haver entre 3 e 5 opções", HttpStatus.BAD_REQUEST);
        }

        long correctCount = options.stream()
                .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                .count();

        if (correctCount < 1) {
            throw new ApiException("Deve haver pelo menos uma opção correta", HttpStatus.BAD_REQUEST);
        }
        if (correctCount == options.size()) {
            throw new ApiException("Deve haver pelo menos uma opção incorreta", HttpStatus.BAD_REQUEST);
        }

        validateCommonOptions(options, statement);
    }

    private void validateCommonOptions(List<OptionDTO> options, String statement) {
        Set<String> uniqueOptions = new HashSet<>();

        for (OptionDTO option : options) {

            if (option.getOption().equalsIgnoreCase(statement)) {
                throw new ApiException("Uma opção não pode ser igual ao enunciado", HttpStatus.BAD_REQUEST);
            }

            if (!uniqueOptions.add(option.getOption().toLowerCase())) {
                throw new ApiException("Existem opções duplicadas: " + option.getOption(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void adjustTaskOrdersForInsertion(Long courseId, Integer newOrder) {
        List<Task> tasksToAdjust = taskRepository.findByCourseIdAndOrderGreaterThanEqual(courseId, newOrder);

        tasksToAdjust.forEach(task -> {
            task.setOrder(task.getOrder() + 1);
            taskRepository.save(task);
        });
    }

    private List<Option> convertToOptions(List<OptionDTO> optionDTOs) {
        return optionDTOs.stream().map(dto -> {
            Option option = new Option();
            option.setText(dto.getOption());
            option.setAnswer(dto.getIsCorrect());
            return option;
        }).toList();
    }



}
