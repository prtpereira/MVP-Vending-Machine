package com.kabisa.vendingmachine.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kabisa.vendingmachine.models.User;
import com.kabisa.vendingmachine.security.GetAuthenticatedUserInteractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final GetAuthenticatedUserInteractor getAuthenticatedUserInteractor;

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = getAuthenticatedUserInteractor.call(request);
            Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
            String authorizationHeader = request.getHeader("Authorization");
            String refreshToken = authorizationHeader.substring("Bearer ".length());

            String accessToken = JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 600 * 1000))
                    .withIssuer(request.getRequestURL().toString())
                    .withClaim("roles", List.of(user.getRole()))
                    .sign(algorithm);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", accessToken);
            tokens.put("refresh_token", refreshToken);
            response.setContentType("application/json");

            new ObjectMapper().writeValue(response.getOutputStream(), tokens);

        } catch (Exception exception) {
            log.error("Error logging in: {}", exception.getMessage());
            response.setHeader("error", exception.getMessage());
            response.setStatus(403, "Forbidden");

            Map<String, String> error = new HashMap<>();
            error.put("error_message", exception.getMessage());
            response.setContentType("application/json");

            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
    }
}
