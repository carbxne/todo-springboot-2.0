package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            throw new RuntimeException("Email not found from OAuth2 provider");
        }

        // ищем пользователя в БД
        Optional<User> userOptional = userRepository.findByUsername(email);

        User user;
        if (userOptional.isEmpty()) {
            // если нет — создаём
            user = new User();
            user.setUsername(email);
            user.setPassword("{noop}OAUTH2_USER"); // нет реального пароля
            user.setRole("USER");
            userRepository.save(user);
        } else {
            user = userOptional.get();
        }

        // возвращаем DefaultOAuth2User для Spring Security
        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_USER"),
                oAuth2User.getAttributes(),
                "email"
        );
    }
}
