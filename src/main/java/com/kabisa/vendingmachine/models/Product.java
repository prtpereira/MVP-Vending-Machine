package com.kabisa.vendingmachine.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

import static javax.persistence.GenerationType.AUTO;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {
    @Id
    @GeneratedValue(strategy = AUTO)
    private UUID id;
    private String name;
    @JsonProperty(value = "seller_id")
    private UUID sellerId;
    private int cost;
    @JsonProperty(value = "amount_available")
    private int amountAvailable;
    private LocalDateTime deletedAt = null;

    public Product(UUID id, String name, UUID sellerId, int cost, int amountAvailable) {
        this.id = id;
        this.name = name;
        this.sellerId = sellerId;
        this.cost = cost;
        this.amountAvailable = amountAvailable;
    }

    public Product(UUID id, Product product) {
        this(id, product.name, product.sellerId, product.cost, product.amountAvailable);
    }

    public Product() {
        super();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public void setSellerId(UUID sellerId) {
        this.sellerId = sellerId;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getAmountAvailable() {
        return amountAvailable;
    }

    public void setAmountAvailable(int amountAvailable) {
        this.amountAvailable = amountAvailable;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
