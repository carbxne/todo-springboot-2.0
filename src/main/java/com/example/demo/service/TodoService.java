package com.example.demo.service;

import com.example.demo.model.Todo;
import com.example.demo.model.User;
import com.example.demo.repository.TodoRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    public List<Todo> getTodosForUser(String username) {
        return todoRepository.findByUser_Username(username);
    }

    public Todo addTodoForUser(String username, String title, String description) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setDone(false);
        todo.setUser(user);
        return todoRepository.save(todo);
    }

    public void deleteTodoForUser(String username, int id) {
        Todo todo = getOwnedTodo(username, id);
        todoRepository.delete(todo);
    }

    public Todo toggleDoneForUser(String username, int id, boolean done) {
        Todo todo = getOwnedTodo(username, id);
        todo.setDone(done);
        return todoRepository.save(todo);
    }

    public Todo updateDetailsForUser(String username, int id, String title, String description) {
        Todo todo = getOwnedTodo(username, id);
        todo.setTitle(title);
        todo.setDescription(description);
        return todoRepository.save(todo);
    }

    public Todo getTodoByIdForUser(String username, int id) {
        return getOwnedTodo(username, id);
    }

    private Todo getOwnedTodo(String username, int id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Задача не найдена"));
        if (todo.getUser() == null || todo.getUser().getUsername() == null || !todo.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа к этой задаче");
        }
        return todo;
    }
}
