package com.eotd.repository;

import com.eotd.model.EarringOfTheDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EarringOfTheDayRepository extends JpaRepository<EarringOfTheDay, Long> {

    List<EarringOfTheDay> findByDateOrderByDisplayOrderAsc(LocalDate date);

    boolean existsByInstagramPostUrl(String instagramPostUrl);

    /**
     * Returns all EOTD entries for the most recent date that has at least one
     * entry, ordered by {@code displayOrder}.  Used as a fallback when there is
     * no entry for today.
     */
    @Query("SELECT e FROM EarringOfTheDay e " +
           "WHERE e.date = (SELECT MAX(e2.date) FROM EarringOfTheDay e2) " +
           "ORDER BY e.displayOrder ASC")
    List<EarringOfTheDay> findMostRecent();
}
