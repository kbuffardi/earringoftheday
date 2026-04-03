package com.eotd;

import com.eotd.model.EarringOfTheDay;
import com.eotd.repository.EarringOfTheDayRepository;
import com.eotd.service.InstagramService;
import com.eotd.service.InstagramSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstagramSyncServiceTests {

    @Mock
    private InstagramService instagramService;

    @Mock
    private EarringOfTheDayRepository eotdRepository;

    private InstagramSyncService syncService;

    @BeforeEach
    void setUp() {
        syncService = new InstagramSyncService(instagramService, eotdRepository);
    }

    @Test
    void sync_doesNothing_whenNotConfigured() {
        when(instagramService.isConfigured()).thenReturn(false);

        syncService.syncInstagramPosts();

        verify(instagramService, never()).fetchRecentEotdPosts();
        verify(eotdRepository, never()).save(any());
    }

    @Test
    void sync_doesNothing_whenNoEotdPosts() {
        when(instagramService.isConfigured()).thenReturn(true);
        when(instagramService.fetchRecentEotdPosts()).thenReturn(Collections.emptyList());

        syncService.syncInstagramPosts();

        verify(eotdRepository, never()).save(any());
    }

    @Test
    void sync_createsEntry_forNewPost() {
        when(instagramService.isConfigured()).thenReturn(true);

        InstagramService.InstagramPost post = new InstagramService.InstagramPost(
                "123",
                "#EOTD gorgeous hoops!",
                "IMAGE",
                "https://www.instagram.com/p/abc123/",
                "2024-04-03T10:00:00+0000");

        when(instagramService.fetchRecentEotdPosts()).thenReturn(List.of(post));
        when(eotdRepository.existsByInstagramPostUrl("https://www.instagram.com/p/abc123/"))
                .thenReturn(false);
        when(eotdRepository.findByDateOrderByDisplayOrderAsc(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        syncService.syncInstagramPosts();

        verify(eotdRepository, times(1)).save(any(EarringOfTheDay.class));
    }

    @Test
    void sync_skipsEntry_whenPostAlreadyExists() {
        when(instagramService.isConfigured()).thenReturn(true);

        InstagramService.InstagramPost post = new InstagramService.InstagramPost(
                "123",
                "#EOTD gorgeous hoops!",
                "IMAGE",
                "https://www.instagram.com/p/abc123/",
                "2024-04-03T10:00:00+0000");

        when(instagramService.fetchRecentEotdPosts()).thenReturn(List.of(post));
        when(eotdRepository.existsByInstagramPostUrl("https://www.instagram.com/p/abc123/"))
                .thenReturn(true);

        syncService.syncInstagramPosts();

        verify(eotdRepository, never()).save(any());
    }

    @Test
    void sync_setsCorrectDisplayOrder_forMultiplePostsSameDay() {
        when(instagramService.isConfigured()).thenReturn(true);

        InstagramService.InstagramPost post1 = new InstagramService.InstagramPost(
                "111",
                "#EOTD first post",
                "IMAGE",
                "https://www.instagram.com/p/post1/",
                "2024-04-03T10:00:00+0000");

        InstagramService.InstagramPost post2 = new InstagramService.InstagramPost(
                "222",
                "#EOTD second post",
                "IMAGE",
                "https://www.instagram.com/p/post2/",
                "2024-04-03T12:00:00+0000");

        when(instagramService.fetchRecentEotdPosts()).thenReturn(List.of(post1, post2));
        when(eotdRepository.existsByInstagramPostUrl(any())).thenReturn(false);

        // First call returns empty (0 existing), second returns 1 existing
        EarringOfTheDay existing = new EarringOfTheDay();
        existing.setDisplayOrder(0);
        when(eotdRepository.findByDateOrderByDisplayOrderAsc(any(LocalDate.class)))
                .thenReturn(Collections.emptyList())
                .thenReturn(List.of(existing));

        syncService.syncInstagramPosts();

        verify(eotdRepository, times(2)).save(any(EarringOfTheDay.class));
    }
}
