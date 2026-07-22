package com.vermeg.testbed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class TestbedApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestbedApplication.class, args);
    }

    // Initialise la base H2 au démarrage : l'application est réellement fonctionnelle.
    @Bean
    CommandLineRunner initDatabase(JdbcTemplate jdbc) {
        return args -> {
            jdbc.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "username VARCHAR(100), " +
                    "role VARCHAR(50))");
            jdbc.execute("CREATE TABLE IF NOT EXISTS tasks (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "title VARCHAR(200), " +
                    "priority INT, " +
                    "done BOOLEAN)");
            jdbc.update("INSERT INTO users (username, role) VALUES (?, ?)", "admin", "ADMIN");
            jdbc.update("INSERT INTO users (username, role) VALUES (?, ?)", "souhaiel", "USER");
            jdbc.update("INSERT INTO tasks (title, priority, done) VALUES (?, ?, ?)", "Preparer soutenance", 1, false);
            jdbc.update("INSERT INTO tasks (title, priority, done) VALUES (?, ?, ?)", "Tester la plateforme", 2, false);
        };
    }
}
