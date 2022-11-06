package com.kabisa.vendingmachine;

import com.kabisa.vendingmachine.models.Product;
import com.kabisa.vendingmachine.models.Role;
import com.kabisa.vendingmachine.models.User;
import com.kabisa.vendingmachine.service.ProductService;
import com.kabisa.vendingmachine.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@SpringBootApplication
public class VendingMachineApplication {

    public static void main(String[] args) {
        SpringApplication.run(VendingMachineApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // For local testing purposes only
    @Bean
    CommandLineRunner run(UserService userService, ProductService productService) {
        return args -> {
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
        };
    }


}
