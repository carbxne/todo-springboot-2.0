package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class UserHtmlController {

    private final UserService userService;

    public UserHtmlController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model) {
        return userService.findByUsername(username)
                .filter(user -> userService.checkPassword(user, password))
                .map(user -> "redirect:/auth/welcome")
                .orElseGet(() -> {
                    model.addAttribute("error", "Invalid username or password");
                    return "login";
                });
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // register.html
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/auth/login";
    }

}
