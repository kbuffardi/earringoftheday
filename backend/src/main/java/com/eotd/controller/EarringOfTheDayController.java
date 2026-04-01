package com.eotd.controller;

import com.eotd.model.EarringOfTheDay;
import com.eotd.model.ReferralClick;
import com.eotd.repository.EarringOfTheDayRepository;
import com.eotd.repository.ReferralClickRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eotd")
public class EarringOfTheDayController {

    private static final ZoneId PACIFIC = ZoneId.of("America/Los_Angeles");

    private final EarringOfTheDayRepository eotdRepository;
    private final ReferralClickRepository clickRepository;

    public EarringOfTheDayController(EarringOfTheDayRepository eotdRepository,
                                     ReferralClickRepository clickRepository) {
        this.eotdRepository = eotdRepository;
        this.clickRepository = clickRepository;
    }

    /** Public: get today's EOTD list (Pacific time), ordered by displayOrder */
    @GetMapping("/today")
    public List<EarringOfTheDay> getToday() {
        LocalDate today = LocalDate.now(PACIFIC);
        return eotdRepository.findByDateOrderByDisplayOrderAsc(today);
    }

    /** Admin: list all EOTD entries */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<EarringOfTheDay> getAll() {
        return eotdRepository.findAll();
    }

    /** Admin: create a new EOTD entry */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EarringOfTheDay> create(@RequestBody EarringOfTheDay eotd) {
        if (eotd.getDisplayOrder() == null) {
            eotd.setDisplayOrder(0);
        }
        EarringOfTheDay saved = eotdRepository.save(eotd);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /** Admin: update an existing EOTD entry */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EarringOfTheDay update(@PathVariable Long id, @RequestBody EarringOfTheDay eotd) {
        if (!eotdRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "EOTD not found");
        }
        eotd.setId(id);
        return eotdRepository.save(eotd);
    }

    /** Admin: delete an EOTD entry */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!eotdRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "EOTD not found");
        }
        eotdRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Public: track a referral click and redirect to the earring's referral link.
     * Records the earring ID, click timestamp, and requester IP address.
     */
    @GetMapping("/{id}/redirect")
    public ResponseEntity<Void> redirect(@PathVariable Long id, HttpServletRequest request) {
        EarringOfTheDay eotd = eotdRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "EOTD not found"));

        String referralLink = eotd.getReferralLink();
        if (referralLink == null || referralLink.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No referral link set for this earring");
        }

        ReferralClick click = new ReferralClick();
        click.setEarringId(id);
        click.setClickTime(Instant.now());
        click.setIpAddress(resolveClientIp(request));
        clickRepository.save(click);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(referralLink));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    /** Admin: get click stats summary for a specific EOTD */
    @GetMapping("/{id}/clicks")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getClickStats(@PathVariable Long id) {
        if (!eotdRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "EOTD not found");
        }
        List<ReferralClick> clicks = clickRepository.findAll().stream()
                .filter(c -> id.equals(c.getEarringId()))
                .toList();
        return Map.of("earringId", id, "totalClicks", clicks.size(), "clicks", clicks);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
