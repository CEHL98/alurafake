package br.com.alura.AluraFake.controller;

import br.com.alura.AluraFake.dto.request.task.TaskDTO;
import br.com.alura.AluraFake.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @PostMapping("/task/new/opentext")
    public ResponseEntity newOpenTextExercise(@Valid @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.createOpenTextTask(taskDTO));
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice(@Valid @RequestBody  TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.createSingleChoiceTask(taskDTO));
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice(@Valid @RequestBody  TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.createMultipleChoiceTask(taskDTO));
    }

}