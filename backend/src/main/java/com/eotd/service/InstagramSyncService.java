package com.eotd.service;

import com.eotd.model.EarringOfTheDay;
import com.eotd.repository.EarringOfTheDayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Periodically syncs recent Instagram posts tagged with {@code #EOTD} from the
 * {@code earringofthedaywithrachel} account into the local
 * {@code earring_of_the_day} table.
 *
 * <ul>
 *   <li>Runs on application startup (after a short initial delay) and then at
 *       the interval configured by {@code instagram.sync-interval-ms}
 *       (default: 30 minutes).</li>
 *   <li>Only creates a new entry if the Instagram permalink is not already
 *       stored.</li>
 *   <li>Gracefully does nothing when no access token is configured.</li>
 * </ul>
 */
@Service
public class InstagramSyncService {

    private static final Logger log = LoggerFactory.getLogger(InstagramSyncService.class);
    private static final ZoneId PACIFIC = ZoneId.of("America/Los_Angeles");
    /** Instagram Graph API returns timestamps in the compact offset format {@code +0000}. */
    private static final DateTimeFormatter INSTAGRAM_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    private final InstagramService instagramService;
    private final EarringOfTheDayRepository eotdRepository;

    public InstagramSyncService(InstagramService instagramService,
                                EarringOfTheDayRepository eotdRepository) {
        this.instagramService = instagramService;
        this.eotdRepository = eotdRepository;
    }

    /**
     * Fetches recent {@code #EOTD}-tagged Instagram posts and, for each one not
     * already present in the database, creates a new {@link EarringOfTheDay}
     * entry with the post's permalink and date.  Admins can then fill in
     * product metadata (brand, product name, referral link, etc.) via the admin
     * page.
     */
    @Scheduled(initialDelayString = "${instagram.sync-initial-delay-ms:10000}",
               fixedDelayString  = "${instagram.sync-interval-ms:1800000}")
    public void syncInstagramPosts() {
        if (!instagramService.isConfigured()) {
            return;
        }

        log.info("Starting Instagram EOTD sync…");
        List<InstagramService.InstagramPost> posts = instagramService.fetchRecentEotdPosts();

        int created = 0;
        for (InstagramService.InstagramPost post : posts) {
            if (eotdRepository.existsByInstagramPostUrl(post.permalink())) {
                continue;
            }

            LocalDate postDate = OffsetDateTime.parse(post.timestamp(), INSTAGRAM_TS)
                    .atZoneSameInstant(PACIFIC)
                    .toLocalDate();

            int order = eotdRepository.findByDateOrderByDisplayOrderAsc(postDate).size();

            EarringOfTheDay eotd = new EarringOfTheDay();
            eotd.setDate(postDate);
            eotd.setInstagramPostUrl(post.permalink());
            eotd.setDisplayOrder(order);

            eotdRepository.save(eotd);
            created++;
            log.info("Created EOTD entry for {} from post {}", postDate, post.permalink());
        }

        log.info("Instagram EOTD sync complete – {} new entries created", created);
    }
}
