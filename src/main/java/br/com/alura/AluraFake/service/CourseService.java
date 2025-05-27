package br.com.alura.AluraFake.service;

import br.com.alura.AluraFake.domain.course.Course;
import br.com.alura.AluraFake.dto.response.course.CourseListItemDTO;
import br.com.alura.AluraFake.dto.request.course.NewCourseDTO;
import br.com.alura.AluraFake.domain.course.Status;
import br.com.alura.AluraFake.exception.ApiException;
import br.com.alura.AluraFake.domain.task.Task;
import br.com.alura.AluraFake.repository.CourseRepository;
import br.com.alura.AluraFake.repository.TaskRepository;
import br.com.alura.AluraFake.domain.task.Type;
import br.com.alura.AluraFake.domain.user.User;
import br.com.alura.AluraFake.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Course createCourse(NewCourseDTO newCourse){
        Optional<User> possibleAuthor = userRepository
                .findByEmail(newCourse.getEmailInstructor())
                .filter(User::isInstructor);

        if(possibleAuthor.isEmpty()) {
            throw new ApiException("O usuário informado não é um instrutor", HttpStatus.BAD_REQUEST);
        }

        Course course = new Course(newCourse.getTitle(), newCourse.getDescription(), possibleAuthor.get());

        courseRepository.save(course);
        return course;
    }

    public  List<CourseListItemDTO> listAllCourses() {
        List<CourseListItemDTO> courses = courseRepository.findAll().stream()
                .map(CourseListItemDTO::new)
                .toList();
        return courses;
    }
        @Transactional
        public Course publishCourse(Long courseId) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new ApiException("Curso não encontrado para o parâmetro informado.", HttpStatus.NOT_FOUND));


            if (course.getStatus() != Status.BUILDING) {
                throw new ApiException("O curso só pode ser publicado se estiver com status BUILDING", HttpStatus.BAD_REQUEST);
            }


            List<Task> tasks = taskRepository.findByCourseId(courseId);

            if (tasks == null || tasks.isEmpty()) {
                throw new ApiException( "O curso não pode ser publicado sem atividades", HttpStatus.BAD_REQUEST);
            }

            validateTaskTypes(tasks);

            validateTaskOrderSequence(tasks);

            course.setStatus(Status.PUBLISHED);
            course.setPublishedAt(LocalDateTime.now());

            return courseRepository.save(course);
        }

        private void validateTaskTypes(List<Task> tasks) {
            Set<Type> taskTypes = tasks.stream()
                    .map(Task::getType)
                    .collect(Collectors.toSet());

            if (!taskTypes.containsAll(EnumSet.allOf(Type.class))) {
                throw new ApiException("O curso deve conter pelo menos uma atividade de cada tipo", HttpStatus.BAD_REQUEST);
            }
        }

        private void validateTaskOrderSequence(List<Task> tasks) {
            List<Integer> orders = tasks.stream()
                    .map(Task::getOrder)
                    .sorted()
                    .collect(Collectors.toList());

            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i) != i + 1) {
                    throw new ApiException("As atividades devem ter orders em sequência",HttpStatus.BAD_REQUEST);
                }
            }
        }
}

