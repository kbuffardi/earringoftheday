package com.eotd.security;

import com.eotd.model.User;
import com.eotd.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final String frontendUrl;

    public OAuth2LoginSuccessHandler(UserService userService,
                                     @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl) {
        this.userService = userService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = extractEmail(oAuth2User);
        String firstName = extractFirstName(oAuth2User);
        String lastName = extractLastName(oAuth2User);

        if (email != null && !email.isBlank()) {
            userService.loginOrRegister(email, firstName, lastName);
        }

        response.sendRedirect(frontendUrl);
    }

    private String extractEmail(OAuth2User user) {
        Object email = user.getAttribute("email");
        return email != null ? email.toString() : null;
    }

    private String extractFirstName(OAuth2User user) {
        // Google / Microsoft provide "given_name"
        Object givenName = user.getAttribute("given_name");
        if (givenName != null) return givenName.toString();
        // Facebook provides "first_name"
        Object firstName = user.getAttribute("first_name");
        if (firstName != null) return firstName.toString();
        // Fallback: split "name" attribute
        Object name = user.getAttribute("name");
        if (name != null) {
            String[] parts = name.toString().split(" ", 2);
            return parts[0];
        }
        return "";
    }

    private String extractLastName(OAuth2User user) {
        // Google / Microsoft provide "family_name"
        Object familyName = user.getAttribute("family_name");
        if (familyName != null) return familyName.toString();
        // Facebook provides "last_name"
        Object lastName = user.getAttribute("last_name");
        if (lastName != null) return lastName.toString();
        // Fallback: split "name" attribute
        Object name = user.getAttribute("name");
        if (name != null) {
            String[] parts = name.toString().split(" ", 2);
            return parts.length > 1 ? parts[1] : "";
        }
        return "";
    }
}
