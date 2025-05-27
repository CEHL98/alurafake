CREATE TABLE IF NOT EXISTS Task (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    type ENUM('OPEN_TEXT', 'SINGLE_CHOICE', 'MULTIPLE_CHOICE') NOT NULL,
    statement VARCHAR(255) NOT NULL,
    `order_task` INT UNSIGNED NOT NULL,
    course_id BIGINT(20) NOT NULL,
    createdAt datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_task_course FOREIGN KEY (course_id) REFERENCES Course(id) ON DELETE CASCADE,
    CONSTRAINT uk_task_course_statement UNIQUE (course_id, statement),
    CONSTRAINT uk_task_course_order UNIQUE (course_id, `order_task`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
