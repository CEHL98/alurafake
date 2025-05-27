package br.com.alura.AluraFake.controller;

import br.com.alura.AluraFake.dto.request.course.NewCourseDTO;
import br.com.alura.AluraFake.dto.response.course.CourseListItemDTO;
import br.com.alura.AluraFake.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService){
        this.courseService = courseService;
    }

    @PostMapping("/course/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse) {
        courseService.createCourse(newCourse);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/course/all")
    public ResponseEntity<List<CourseListItemDTO>> listAllCourses() {
    return  ResponseEntity.ok(courseService.listAllCourses());
    }

    @PostMapping("/course/{id}/publish")
    public ResponseEntity publishCourse(@PathVariable("id") Long id) {
        return ResponseEntity.ok(courseService.publishCourse(id));
    }

}
