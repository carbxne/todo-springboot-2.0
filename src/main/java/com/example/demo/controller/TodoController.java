package com.example.demo.controller;

import com.example.demo.model.Todo;
import com.example.demo.service.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/home")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // Страница со списком задач текущего пользователя
    @GetMapping({"/web",""})
    public String home(Model model, Principal principal) {
        String username = principal.getName();
        List<Todo> todos = todoService.getTodosForUser(username);
        model.addAttribute("todos", todos);
        model.addAttribute("username", username);
        return "home";
    }

    // --- API: добавить задачу ---
    @PostMapping
    @ResponseBody
    public ResponseEntity<Todo> addTodo(@RequestBody Map<String, String> payload, Principal principal) {
        String title = payload.getOrDefault("title", "");
        String description = payload.getOrDefault("description", "");
        Todo todo = todoService.addTodoForUser(principal.getName(), title, description);
        return ResponseEntity.ok(todo);
    }

    // --- API: удалить задачу ---
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteTodo(@PathVariable int id, Principal principal) {
        todoService.deleteTodoForUser(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    // --- API: изменить статус выполненности ---
    @PatchMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Todo> updateStatus(@PathVariable int id, @RequestBody Map<String, Boolean> payload, Principal principal) {
        boolean done = payload.getOrDefault("done", false);
        Todo updated = todoService.toggleDoneForUser(principal.getName(), id, done);
        return ResponseEntity.ok(updated);
    }

    // --- API: редактировать заголовок и описание ---
    @PutMapping("/{id}/edit")
    @ResponseBody
    public ResponseEntity<Todo> editTodo(@PathVariable int id, @RequestBody Map<String, String> payload, Principal principal) {
        String title = payload.getOrDefault("title", "");
        String description = payload.getOrDefault("description", "");
        Todo updated = todoService.updateDetailsForUser(principal.getName(), id, title, description);
        return ResponseEntity.ok(updated);
    }

    // --- API: получить одну задачу ---
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Todo> getTodoById(@PathVariable int id, Principal principal) {
        Todo todo = todoService.getTodoByIdForUser(principal.getName(), id);
        return ResponseEntity.ok(todo);
    }
}
