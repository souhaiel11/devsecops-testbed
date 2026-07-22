package com.vermeg.testbed.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository fonctionnel (partie saine de l'application).
 */
@Repository
public class TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findAll() {
        return jdbcTemplate.queryForList("SELECT * FROM tasks ORDER BY priority");
    }

    public int add(String title, int priority) {
        return jdbcTemplate.update(
                "INSERT INTO tasks (title, priority, done) VALUES (?, ?, ?)",
                title, priority, false);
    }

    public int markDone(int id) {
        return jdbcTemplate.update("UPDATE tasks SET done = true WHERE id = ?", id);
    }
}
