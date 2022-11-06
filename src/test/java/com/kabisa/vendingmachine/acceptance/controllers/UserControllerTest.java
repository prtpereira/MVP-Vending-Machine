package com.kabisa.vendingmachine.acceptance.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kabisa.vendingmachine.models.Product;
import com.kabisa.vendingmachine.utils.AuthenticationMock;
import com.kabisa.vendingmachine.utils.DatabaseMock;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Map;

@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DatabaseMock databaseMock;

    private static String jwt;

    private final static String LIST_USER_ENDPOINT = "/user";
    private final static String LIST_USERS_ENDPOINT = "/users";

    private final static String BUY_PRODUCT_ENDPOINT = "/user/buy";
    private final static String DEPOSIT_COIN_ENDPOINT = "/user/deposit/";
    private final static String GET_PRODUCT_ENDPOINT = "/products/";

    private final static String BAD_REQUEST_ERROR_MSG = "Coin is not valid.";

    private final static String VALID_COIN_20 = "20";
    private final static String VALID_COIN_100 = "100";

    @BeforeAll
    public void BeforeAll() {
        jwt = AuthenticationMock.login();
    }

    @BeforeEach
    public void BeforeEach() {
        databaseMock.populateDB();
        databaseMock.resetAllUsersDeposit();
    }

    @Test
    public void listLoggedUser() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get(LIST_USER_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("andre")));
    }

    @Test
    public void listUsers() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get(LIST_USERS_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(4)));
    }

    @Test
    public void depositIsInvalid() throws Exception {
        final String INVALID_COIN = "90";

        mockMvc.perform(
            MockMvcRequestBuilders
                .post(DEPOSIT_COIN_ENDPOINT + INVALID_COIN)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error_message", Matchers.is(BAD_REQUEST_ERROR_MSG)));
    }

    @Test
    public void depositIsIValid() throws Exception {

        mockMvc.perform(
            MockMvcRequestBuilders
                .post(DEPOSIT_COIN_ENDPOINT + VALID_COIN_100)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.deposit", Matchers.is(Integer.valueOf(VALID_COIN_100))));
    }

    @Test
    public void depositIsIValidAndAccumulatesPreviousValue() throws Exception {
        final Integer ACCUMULATED_VALUE = Integer.parseInt(VALID_COIN_100) + Integer.parseInt(VALID_COIN_20);

        mockMvc.perform(
            MockMvcRequestBuilders
                .post(DEPOSIT_COIN_ENDPOINT + VALID_COIN_100)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.deposit", Matchers.is(Integer.valueOf(VALID_COIN_100))));

        mockMvc.perform(
            MockMvcRequestBuilders
                .post(DEPOSIT_COIN_ENDPOINT + VALID_COIN_20)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.deposit", Matchers.is(ACCUMULATED_VALUE)));
    }

    @Test
    public void buyProduct() throws Exception {
        Product waterProduct = databaseMock.getProductByName("Water Sparkling").get();

        final Map<String, String> BUY_3_WATER_PAYLOAD = Map.of(
                "product_id",waterProduct.getId().toString(),
                "amount", "3");

        mockMvc.perform(
            MockMvcRequestBuilders
                .post(DEPOSIT_COIN_ENDPOINT + VALID_COIN_100)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
            MockMvcRequestBuilders
                .post(DEPOSIT_COIN_ENDPOINT + VALID_COIN_100)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
            MockMvcRequestBuilders
                .post(BUY_PRODUCT_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(BUY_3_WATER_PAYLOAD))
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.total_spent", Matchers.is(135)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.product.cost", Matchers.is(45)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.product.amount_available", Matchers.is(27)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.change", Matchers.is(Arrays.asList(1, 1, 0, 1, 0))));
    }

    @Test
    public void getExistentProduct() throws Exception {
        Product persistedProduct = databaseMock.getProductByName("Water Sparkling").get();

        final String VALID_ID = persistedProduct.getId().toString();

        mockMvc.perform(
            MockMvcRequestBuilders
                .get(GET_PRODUCT_ENDPOINT + VALID_ID)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(VALID_ID)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Water Sparkling")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cost", Matchers.is(45)));
    }

    @Test
    public void returnNotFoundWhenProductDoesNotExist() throws Exception {
        final String UUID = "aaaaaaaa-bbbb-cccc-dddd-b27f30d57ed8";

        mockMvc.perform(
            MockMvcRequestBuilders
                .post(GET_PRODUCT_ENDPOINT + UUID)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
