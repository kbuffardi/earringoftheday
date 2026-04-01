package com.eotd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OAuth2ClientRegistrationConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
            @Value("${GOOGLE_CLIENT_ID:}") String googleClientId,
            @Value("${GOOGLE_CLIENT_SECRET:}") String googleClientSecret,
            @Value("${MICROSOFT_CLIENT_ID:}") String microsoftClientId,
            @Value("${MICROSOFT_CLIENT_SECRET:}") String microsoftClientSecret,
            @Value("${APPLE_CLIENT_ID:}") String appleClientId,
            @Value("${APPLE_CLIENT_SECRET:}") String appleClientSecret,
            @Value("${FACEBOOK_CLIENT_ID:}") String facebookClientId,
            @Value("${FACEBOOK_CLIENT_SECRET:}") String facebookClientSecret) {

        List<ClientRegistration> registrations = new ArrayList<>();

        if (!googleClientId.isBlank() && !googleClientSecret.isBlank()) {
            registrations.add(ClientRegistration.withRegistrationId("google")
                    .clientId(googleClientId)
                    .clientSecret(googleClientSecret)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .scope("openid", "profile", "email")
                    .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                    .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                    .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                    .issuerUri("https://accounts.google.com")
                    .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                    .userNameAttributeName(IdTokenClaimNames.SUB)
                    .clientName("Google")
                    .build());
        }

        if (!microsoftClientId.isBlank() && !microsoftClientSecret.isBlank()) {
            registrations.add(ClientRegistration.withRegistrationId("microsoft")
                    .clientId(microsoftClientId)
                    .clientSecret(microsoftClientSecret)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .scope("openid", "profile", "email")
                    .authorizationUri("https://login.microsoftonline.com/common/oauth2/v2.0/authorize")
                    .tokenUri("https://login.microsoftonline.com/common/oauth2/v2.0/token")
                    .jwkSetUri("https://login.microsoftonline.com/common/discovery/v2.0/keys")
                    .userInfoUri("https://graph.microsoft.com/oidc/userinfo")
                    .userNameAttributeName(IdTokenClaimNames.SUB)
                    .clientName("Microsoft")
                    .build());
        }

        if (!appleClientId.isBlank() && !appleClientSecret.isBlank()) {
            registrations.add(ClientRegistration.withRegistrationId("apple")
                    .clientId(appleClientId)
                    .clientSecret(appleClientSecret)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .scope("name", "email")
                    .authorizationUri("https://appleid.apple.com/auth/authorize?response_mode=form_post")
                    .tokenUri("https://appleid.apple.com/auth/token")
                    .jwkSetUri("https://appleid.apple.com/auth/keys")
                    .userNameAttributeName(IdTokenClaimNames.SUB)
                    .clientName("Apple")
                    .build());
        }

        if (!facebookClientId.isBlank() && !facebookClientSecret.isBlank()) {
            registrations.add(ClientRegistration.withRegistrationId("facebook")
                    .clientId(facebookClientId)
                    .clientSecret(facebookClientSecret)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .scope("email", "public_profile")
                    .authorizationUri("https://www.facebook.com/v12.0/dialog/oauth")
                    .tokenUri("https://graph.facebook.com/v12.0/oauth/access_token")
                    .userInfoUri("https://graph.facebook.com/me?fields=id,name,email,first_name,last_name")
                    .userNameAttributeName("id")
                    .clientName("Facebook")
                    .build());
        }

        if (registrations.isEmpty()) {
            // Return a no-op repository when no providers are configured (e.g. in tests/dev)
            return registrationId -> null;
        }

        return new InMemoryClientRegistrationRepository(registrations);
    }
}
