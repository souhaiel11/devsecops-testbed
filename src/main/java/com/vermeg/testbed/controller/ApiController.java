package com.vermeg.testbed.controller;

import com.vermeg.testbed.repository.TaskRepository;
import com.vermeg.testbed.security.VulnerableSecurity;
import com.vermeg.testbed.service.BuggyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * API REST fonctionnelle de gestion de taches.
 *
 * ================================================================
 * Partie fonctionnelle : /tasks (l'application marche reellement)
 * Partie vulnerable   : endpoints exposant les failles ci-dessus,
 *                       + failles DAST detectees par OWASP ZAP
 *                       Decision attendue ZAP : NOTIFY
 * ================================================================
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final TaskRepository taskRepository;
    private final BuggyService buggyService;
    private final VulnerableSecurity vulnerableSecurity;

    public ApiController(TaskRepository taskRepository,
                         BuggyService buggyService,
                         VulnerableSecurity vulnerableSecurity) {
        this.taskRepository = taskRepository;
        this.buggyService = buggyService;
        this.vulnerableSecurity = vulnerableSecurity;
    }

    // ---------- Partie FONCTIONNELLE (saine) ----------

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @GetMapping("/tasks")
    public List<Map<String, Object>> listTasks() {
        return taskRepository.findAll();
    }

    @PostMapping("/tasks")
    public Map<String, Object> addTask(@RequestParam String title, @RequestParam int priority) {
        taskRepository.add(title, priority);
        return Map.of("created", title, "priority", priority);
    }

    @PutMapping("/tasks/{id}/done")
    public Map<String, Object> done(@PathVariable int id) {
        int updated = taskRepository.markDone(id);
        return Map.of("updated", updated);
    }

    // ---------- Partie VULNERABLE (declenche les failles) ----------

    // Expose l'injection SQL (BLOCKER #1)
    @GetMapping("/users/search")
    public List<Map<String, Object>> searchUser(@RequestParam String username) {
        return vulnerableSecurity.findUser(username);
    }

    // Expose la division par zero (BUG #1)
    @GetMapping("/average")
    public int average(@RequestParam int total, @RequestParam int count) {
        return buggyService.average(total, count);
    }

    // DAST ZAP #1 : XSS reflechi - entree renvoyee sans echappement (java:S5131)
    @GetMapping(value = "/greet", produces = "text/html")
    public String greet(@RequestParam String name) {
        return "<html><body><h1>Bonjour " + name + "</h1></body></html>";
    }

    // DAST ZAP #2 : Redirection ouverte (java:S5146)
    @GetMapping("/redirect")
    public void redirect(@RequestParam String url, javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {
        resp.sendRedirect(url);
    }

    // DAST ZAP #3 : Divulgation d'information / stack trace exposee
    @GetMapping("/debug")
    public String debug(@RequestParam String path) {
        try {
            return "Bytes: " + buggyService.readByte(path);
        } catch (Exception e) {
            // Renvoie la stack complete au client : fuite d'information
            StringBuilder sb = new StringBuilder(e.toString());
            for (StackTraceElement el : e.getStackTrace()) {
                sb.append("\n").append(el.toString());
            }
            return sb.toString();
        }
    }

    // DAST ZAP #4 : Cookie sans attribut sécurisé / pas de HttpOnly
    @GetMapping("/set-cookie")
    public void setCookie(javax.servlet.http.HttpServletResponse resp) {
        javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("session", "abc123");
        // ni Secure ni HttpOnly
        resp.addCookie(cookie);
    }

    // DAST ZAP #5 : Injection de commande exposee (BLOCKER #5)
    @GetMapping("/ping")
    public String ping(@RequestParam String host) throws Exception {
        vulnerableSecurity.runPing(host);
        return "ping lance vers " + host;
    }

    // DAST ZAP #6 : Path traversal expose (BLOCKER #6)
    @GetMapping("/read")
    public String read(@RequestParam String file) throws Exception {
        return vulnerableSecurity.readFile(file);
    }
}
