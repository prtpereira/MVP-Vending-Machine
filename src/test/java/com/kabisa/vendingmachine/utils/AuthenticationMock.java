package com.kabisa.vendingmachine.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class AuthenticationMock {
    private final static String USERNAME = "andre";
    private final static String URL_ISSUER = "http://localhost:8081/api/login";
    private final static List<String> ROLES = List.of("BUYER");

    public static String login() {
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

        return JWT.create()
                .withSubject(USERNAME)
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 600 * 1000))
                .withIssuer(URL_ISSUER)
                .withClaim("roles", ROLES)
                .sign(algorithm);
    }
}
