package com.eotd.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "referral_click")
public class ReferralClick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long earringId;

    @Column(nullable = false)
    private Instant clickTime;

    private String ipAddress;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEarringId() { return earringId; }
    public void setEarringId(Long earringId) { this.earringId = earringId; }

    public Instant getClickTime() { return clickTime; }
    public void setClickTime(Instant clickTime) { this.clickTime = clickTime; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
