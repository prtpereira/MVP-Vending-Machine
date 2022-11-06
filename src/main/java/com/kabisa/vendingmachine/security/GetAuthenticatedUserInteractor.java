package com.kabisa.vendingmachine.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kabisa.vendingmachine.models.User;
import com.kabisa.vendingmachine.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor @Slf4j
public class GetAuthenticatedUserInteractor {
    private final UserService userService;

    public User call(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify((refreshToken));
                String username = decodedJWT.getSubject();

                return userService.getUser(username);
            }
            catch (Exception exception){
                log.error("Error on like operation. Exception: {}", exception.getMessage());
                throw exception;
            }
        } else {
            throw new RuntimeException("Access_token is missing");
        }
    }
}

