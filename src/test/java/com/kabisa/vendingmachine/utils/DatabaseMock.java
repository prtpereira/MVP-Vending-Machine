package com.kabisa.vendingmachine.utils;

import com.kabisa.vendingmachine.models.Product;
import com.kabisa.vendingmachine.models.Role;
import com.kabisa.vendingmachine.models.User;
import com.kabisa.vendingmachine.service.ProductService;
import com.kabisa.vendingmachine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DatabaseMock {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    public void populateDB() {
        Role buyer = new Role(null, "BUYER");
        Role seller = new Role(null, "SELLER");

        userService.saveRole(buyer);
        userService.saveRole(seller);

        User seller_andre = new User(UUID.randomUUID(), "Andre Pereira", "andre", "123", seller, 0);

        userService.saveUser( seller_andre );
        userService.saveUser(new User(null, "John Doe", "john", "123", buyer, 0));
        userService.saveUser(new User(null, "Sarah Connor", "sarah", "123", buyer, 0));
        userService.saveUser(new User(null, "Harold Finch", "harold", "123", seller, 0));

        productService.addProduct(new Product(null, "Water Sparkling", seller_andre.getId(), 45, 30));
        productService.addProduct(new Product(null, "Soda Coke", seller_andre.getId(), 100, 10));
        productService.addProduct(new Product(null, "Chocolate ABC", seller_andre.getId(), 150, 10));
        productService.addProduct(new Product(null, "Cookies XYZ", seller_andre.getId(), 170, 20));
    }

    public Optional<Product> getProductByName(String productName) {
        return productService.getProduct(productName);
    }

    public void resetAllUsersDeposit() {
        List<User> users = userService.getUsers();
        users.forEach(user -> userService.resetDeposit(user.getUsername()));
    }
}
