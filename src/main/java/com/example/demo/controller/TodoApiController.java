package com.example.demo.controller;

import com.example.demo.model.Todo;
import com.example.demo.service.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
public class TodoApiController {

    private final TodoService todoService;

    public TodoApiController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getTodos(Principal principal) {
        return ResponseEntity.ok(todoService.getTodosForUser(principal.getName()));
    }

    @PostMapping
    public ResponseEntity<Todo> addTodo(@RequestBody Map<String, String> payload, Principal principal) {
        Todo todo = todoService.addTodoForUser(principal.getName(),
                payload.getOrDefault("title", ""),
                payload.getOrDefault("description", "")
        );
        return ResponseEntity.ok(todo);
    }
}
