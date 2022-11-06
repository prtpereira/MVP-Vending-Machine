package com.kabisa.vendingmachine.controllers;

import com.kabisa.vendingmachine.models.User;
import com.kabisa.vendingmachine.representers.BuyProductInputRepresenter;
import com.kabisa.vendingmachine.representers.BuyProductOutputRepresenter;
import com.kabisa.vendingmachine.security.GetAuthenticatedUserInteractor;
import com.kabisa.vendingmachine.service.ProductService;
import com.kabisa.vendingmachine.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final GetAuthenticatedUserInteractor getAuthenticatedUserInteractor;
    private  final ProductService productService;
    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<User> getUser(HttpServletRequest request) {
        try {
            User user = getAuthenticatedUserInteractor.call(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.ok().body(user);
            }
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/user")
    public ResponseEntity<Optional<User>> addUser(@RequestBody User user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user").toUriString());
        Optional<User> savedUser = userService.saveUser(user);
        if (savedUser.isPresent()) {
            return ResponseEntity.created(uri).body(savedUser);
        } else {
            JSONObject conflictMessage = new JSONObject();
            conflictMessage.put("error_code", 409);
            conflictMessage.put("error_message", "Username already taken.");
            return new ResponseEntity(conflictMessage, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @PostMapping("/user/buy")
    public ResponseEntity<BuyProductOutputRepresenter> buyProduct(HttpServletRequest request, @RequestBody BuyProductInputRepresenter buyProductInput) {
        try {
            User user = getAuthenticatedUserInteractor.call(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            BuyProductOutputRepresenter outputBody = productService.buyProduct(user.getUsername(), buyProductInput.productId, buyProductInput.amount);

            if (outputBody.getProduct() != null) {
                return ResponseEntity.ok().body(outputBody);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/user/deposit/{coin}")
    public ResponseEntity<?> depositCoin(HttpServletRequest request, @PathVariable Integer coin) {
        try {
            User user = getAuthenticatedUserInteractor.call(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                Optional<User> updatedUser = userService.depositCoin(user.getUsername(), coin);

                if (updatedUser.isPresent()) {
                    return ResponseEntity.ok().body(updatedUser);
                } else {
                    Map<String, String> outputMsg = new HashMap<>();
                    outputMsg.put("error_message", "Coin is not valid.");
                    return ResponseEntity.badRequest().body(outputMsg);
                }
            }
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/user/deposit/reset")
    public ResponseEntity<Object> resetDeposit(HttpServletRequest request) {
        try {
            User user = getAuthenticatedUserInteractor.call(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                int valueReturned = userService.resetDeposit(user.getUsername());

                Map<String, Integer> outputBody = new HashMap<>();
                outputBody.put("value_returned", valueReturned);
                return ResponseEntity.ok().body(outputBody);
            }
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
