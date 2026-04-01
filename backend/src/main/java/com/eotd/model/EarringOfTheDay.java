package com.eotd.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "earring_of_the_day")
public class EarringOfTheDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    private String brand;

    private String productName;

    @Column(length = 2048)
    private String referralLink;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(length = 2048)
    private String productImageUrl;

    @Column(length = 2048)
    private String instagramPostUrl;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getReferralLink() { return referralLink; }
    public void setReferralLink(String referralLink) { this.referralLink = referralLink; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }

    public String getInstagramPostUrl() { return instagramPostUrl; }
    public void setInstagramPostUrl(String instagramPostUrl) { this.instagramPostUrl = instagramPostUrl; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
