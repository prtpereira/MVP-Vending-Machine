package com.kabisa.vendingmachine.service;

import com.kabisa.vendingmachine.models.Product;
import com.kabisa.vendingmachine.representers.BuyProductOutputRepresenter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductService {
    boolean isCoinValid(int coin);
    List<Product> getProducts();
    Optional<Product> getProduct(UUID productId);
    Optional<Product> getProduct(String productName);
    void deleteProduct(UUID productId);
    Optional<Product> updateProduct(UUID productId, Product product);
    Product addProduct(Product product);
    Product removeProductAmount(String name, int amount);
    BuyProductOutputRepresenter buyProduct(String username, UUID productId, int amount);
}
