package com.eotd.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Fetches recent media posts from the Instagram Graph API for the configured
 * account and filters them to only those whose caption contains {@code #eotd}
 * (case-insensitive).
 *
 * <p>Requires the environment variable {@code INSTAGRAM_ACCESS_TOKEN} to be set
 * with a valid long-lived token for the {@code earringofthedaywithrachel} account.
 */
@Service
public class InstagramService {

    private static final Logger log = LoggerFactory.getLogger(InstagramService.class);
    private static final String GRAPH_API_BASE = "https://graph.instagram.com/v18.0";
    private static final int FETCH_LIMIT = 50;

    @Value("${instagram.access-token:}")
    private String accessToken;

    private final RestTemplate restTemplate;

    public InstagramService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /** Returns {@code true} when an access token has been configured. */
    public boolean isConfigured() {
        return accessToken != null && !accessToken.isBlank();
    }

    /**
     * Fetches recent posts from the Instagram account associated with the
     * configured access token and returns those whose captions contain
     * {@code #eotd} (case-insensitive).
     *
     * @return list of matching posts, newest first; empty list on any error or
     *         when not configured
     */
    @SuppressWarnings("unchecked")
    public List<InstagramPost> fetchRecentEotdPosts() {
        if (!isConfigured()) {
            log.debug("Instagram access token not configured; skipping fetch");
            return Collections.emptyList();
        }

        String url = GRAPH_API_BASE + "/me/media"
                + "?fields=id,caption,media_type,permalink,timestamp"
                + "&limit=" + FETCH_LIMIT
                + "&access_token=" + accessToken;

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || !response.containsKey("data")) {
                log.warn("Instagram API returned empty or unexpected response");
                return Collections.emptyList();
            }

            List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");

            return data.stream()
                    .filter(post -> {
                        String caption = (String) post.get("caption");
                        return caption != null && caption.toLowerCase().contains("#eotd");
                    })
                    .map(post -> new InstagramPost(
                            (String) post.get("id"),
                            (String) post.get("caption"),
                            (String) post.get("media_type"),
                            (String) post.get("permalink"),
                            (String) post.get("timestamp")))
                    .toList();
        } catch (Exception e) {
            log.error("Failed to fetch Instagram posts: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /** Immutable value object representing a single Instagram media post. */
    public record InstagramPost(
            String id,
            String caption,
            String mediaType,
            String permalink,
            String timestamp) {
    }
}
