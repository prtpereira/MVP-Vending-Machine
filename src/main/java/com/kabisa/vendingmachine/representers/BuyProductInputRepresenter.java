package com.kabisa.vendingmachine.representers;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class BuyProductInputRepresenter {
    @JsonProperty("product_id")
    public UUID productId;
    public Integer amount;
}
