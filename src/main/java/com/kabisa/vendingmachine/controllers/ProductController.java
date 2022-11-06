package com.kabisa.vendingmachine.controllers;

import com.kabisa.vendingmachine.models.Product;
import com.kabisa.vendingmachine.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok().body(productService.getProducts());
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return ResponseEntity.ok().body(productService.addProduct(product));
    }

    @PutMapping("/{product_id}")
    public ResponseEntity<?> updateProduct(@PathVariable("product_id") UUID productId, @RequestBody Product product) {
        Optional<Product> updatedProduct = productService.updateProduct(productId, product);

        if (updatedProduct.isPresent()) {
            return ResponseEntity.ok().body(updatedProduct);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{product_id}")
    public ResponseEntity<Product> getUser(@PathVariable("product_id") UUID productId) {
        Optional<Product> product = productService.getProduct(productId);

        if (product.isPresent()) {
            return ResponseEntity.ok().body(product.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{product_id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("product_id") UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().body(null);
    }
}
