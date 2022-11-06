package com.kabisa.vendingmachine.service;

import com.kabisa.vendingmachine.models.Product;
import com.kabisa.vendingmachine.models.User;
import com.kabisa.vendingmachine.repositories.ProductRepository;
import com.kabisa.vendingmachine.repositories.UserRepository;
import com.kabisa.vendingmachine.representers.BuyProductOutputRepresenter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {
    private static final ArrayList<Integer> VALID_COINS = new ArrayList<>(Arrays.asList(5, 10, 20, 50, 100));

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProduct(UUID productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Optional<Product> getProduct(String productName) {
        Product product =  productRepository.findByName(productName);

        if (product != null) {
            return Optional.of(product);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Product addProduct(Product product) {
        Product existentProduct = productRepository.findByName(product.getName());

        if (existentProduct == null)
            return productRepository.save(product);

        return existentProduct;
    }

    @Override
    public Product removeProductAmount(String name, int amount) {
        Product product = productRepository.findByName(name);
        int targetAmount = Math.max(product.getAmountAvailable() - amount, 0);
        product.setAmountAvailable(targetAmount);
        return product;
    }

    @Override
    public void deleteProduct(UUID productId) {
        Optional<Product> product = productRepository.findById(productId);

        product.ifPresent(p -> p.setDeletedAt(LocalDateTime.now()));
    }

    @Override
    public Optional<Product> updateProduct(UUID id, Product product) {
        Optional<Product> persistedProduct = productRepository.findById(id);

        if (persistedProduct.isPresent()) {
            Product updatedProduct = new Product(id, product);
            return Optional.of(productRepository.save(updatedProduct));
        }

        return Optional.empty();
    }

    @Override
    public BuyProductOutputRepresenter buyProduct(String username, UUID productId, int amount) {
        Optional<Product> product = productRepository.findById(productId);

        if (product.isPresent()) {

            User user = userRepository.findByUsername(username);
            int userDepositAvailable = user.getDeposit();
            int productCost = product.get().getCost();
            int productAmountAvailable = product.get().getAmountAvailable();
            int totalSpent = 0;

            for (int amountToPurchase = amount; amountToPurchase > 0; amountToPurchase--) {
                if (userDepositAvailable >= (amountToPurchase * productCost) && productAmountAvailable >= amountToPurchase) {
                    product.get().setAmountAvailable(productAmountAvailable - amountToPurchase);
                    totalSpent = amountToPurchase * productCost;
                    break;
                }
            }

            int charge = userDepositAvailable - totalSpent;
            user.setDeposit(0);

            List<Integer> chargeArray = new ArrayList<>(Collections.nCopies(VALID_COINS.size(), 0));
            for (int idx = VALID_COINS.size() - 1; idx >= 0; idx--) {
                int numberOfCoins = charge / VALID_COINS.get(idx);
                chargeArray.set(idx, numberOfCoins);
                charge -= numberOfCoins * VALID_COINS.get(idx);
            }

            return new BuyProductOutputRepresenter(totalSpent, product.get(), chargeArray);
        }
        return new BuyProductOutputRepresenter();
    }

    public boolean isCoinValid(int coin) {
        return VALID_COINS.contains(coin);
    }
}