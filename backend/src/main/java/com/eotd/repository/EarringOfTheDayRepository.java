package com.eotd.repository;

import com.eotd.model.EarringOfTheDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EarringOfTheDayRepository extends JpaRepository<EarringOfTheDay, Long> {

    List<EarringOfTheDay> findByDateOrderByDisplayOrderAsc(LocalDate date);
}
