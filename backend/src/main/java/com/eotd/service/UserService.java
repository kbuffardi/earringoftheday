package com.eotd.service;

import com.eotd.model.User;
import com.eotd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final List<String> defaultAdminEmails;

    public UserService(UserRepository userRepository,
                       @Value("${app.admin-emails:redrachelmason@gmail.com,mydatacollection@gmail.com}") String adminEmails) {
        this.userRepository = userRepository;
        this.defaultAdminEmails = Arrays.stream(adminEmails.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new user or updates an existing one on OAuth2 login.
     */
    public User loginOrRegister(String email, String firstName, String lastName) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            User user = existing.get();
            // Update name fields in case they changed
            user.setFirstName(firstName);
            user.setLastName(lastName);
            return userRepository.save(user);
        }
        User user = new User(email, firstName, lastName);
        if (defaultAdminEmails.contains(email.toLowerCase())) {
            user.setRole(User.Role.ADMIN);
        }
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> searchUsers(String query) {
        if (query == null || query.isBlank()) {
            return userRepository.findAll();
        }
        return userRepository
                .findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        query, query, query);
    }

    public boolean isDefaultAdmin(String email) {
        return defaultAdminEmails.contains(email.toLowerCase());
    }
}
