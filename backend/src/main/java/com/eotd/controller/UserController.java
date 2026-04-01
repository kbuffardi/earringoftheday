package com.eotd.controller;

import com.eotd.model.User;
import com.eotd.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Email not available from provider"));
        }
        return userService.findByEmail(email)
                .map(user -> ResponseEntity.ok(toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me/notifications")
    public ResponseEntity<?> updateNotifications(@AuthenticationPrincipal OAuth2User oAuth2User,
                                                  @RequestBody Map<String, String> body) {
        if (oAuth2User == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        String email = oAuth2User.getAttribute("email");
        String preferenceStr = body.get("notificationPreference");
        if (preferenceStr == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "notificationPreference is required"));
        }
        User.NotificationPreference preference;
        try {
            preference = User.NotificationPreference.valueOf(preferenceStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid preference. Must be DAILY, WEEKLY, or NONE"));
        }
        return userService.findByEmail(email)
                .map(user -> {
                    user.setNotificationPreference(preference);
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
