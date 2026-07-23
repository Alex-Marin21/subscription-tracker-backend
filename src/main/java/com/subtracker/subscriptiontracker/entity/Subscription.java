package com.subtracker.subscriptiontracker.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    private String currency;

    @Column(name = "billing_cycle")
    private String billingCycle; // MONTHLY, YEARLY

    @Column(name = "next_renewal_date")
    private LocalDate nextRenewalDate;

    private String category;

    // Constructors
    public Subscription() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }

    public LocalDate getNextRenewalDate() { return nextRenewalDate; }
    public void setNextRenewalDate(LocalDate nextRenewalDate) { this.nextRenewalDate = nextRenewalDate; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
