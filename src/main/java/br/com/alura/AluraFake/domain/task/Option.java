package br.com.alura.AluraFake.domain.task;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "options")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option", nullable = false, length = 80)
    private String
            description;

    @Column(nullable = false)
    private boolean answer = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", foreignKey = @ForeignKey(name = "fk_option_task"))
    private Task task;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Option() {
    }

    public Option(String description, boolean answer, Task task) {
        this.description = description;
        this.answer = answer;
        this.task = task;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return description;
    }

    public void setText(String description) {
        this.description = description;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}