package br.com.alura.AluraFake.repository;

import br.com.alura.AluraFake.domain.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCourseId(Long courseId);

    @Query("SELECT MAX(t.order) FROM Task t WHERE t.course.id = :courseId")
    Optional<Integer> findMaxOrderByCourseId(@Param("courseId") Long courseId);

    List<Task> findByCourseIdAndOrderGreaterThanEqual(Long courseId, Integer order);
}
