package com.eotd.controller;

import com.eotd.model.User;
import com.eotd.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> searchUsers(
            @RequestParam(required = false) String query) {
        List<Map<String, Object>> users = userService.searchUsers(query)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id,
                                             @RequestBody Map<String, String> body) {
        String roleStr = body.get("role");
        if (roleStr == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "role is required"));
        }
        User.Role role;
        try {
            role = User.Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role. Must be ADMIN or SUBSCRIBER"));
        }
        return userService.findById(id)
                .map(user -> {
                    user.setRole(role);
                    return ResponseEntity.ok(toDto(userService.save(user)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private Map<String, Object> toDto(User user) {
        return Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "firstName", user.getFirstName() != null ? user.getFirstName() : "",
                "lastName", user.getLastName() != null ? user.getLastName() : "",
                "registrationDate", user.getRegistrationDate().toString(),
                "role", user.getRole().name(),
                "notificationPreference", user.getNotificationPreference().name()
        );
    }
}
