package com.kabisa.vendingmachine.representers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kabisa.vendingmachine.models.Product;
import java.util.List;

public class BuyProductOutputRepresenter {
    public int getTotalSpent() {
        return totalSpent;
    }

    public Product getProduct() {
        return product;
    }

    public List<Integer> getChange() {
        return change;
    }

    @JsonProperty("total_spent")
    private int totalSpent;
    @JsonProperty("product")
    private Product product;
    @JsonProperty("change")
    private List<Integer> change;

    public BuyProductOutputRepresenter() {
        this.totalSpent = 0;
        this.product = null;
        this.change = null;
    }

    public BuyProductOutputRepresenter(int totalSpent, Product product, List<Integer> change) {
        this.totalSpent = totalSpent;
        this.product = product;
        this.change = change;
    }
}
