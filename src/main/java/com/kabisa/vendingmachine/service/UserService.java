package com.kabisa.vendingmachine.service;

import com.kabisa.vendingmachine.models.Role;
import com.kabisa.vendingmachine.models.User;
import java.util.List;
import java.util.Optional;

public interface UserService{
    User getUser(String username);
    Optional<User> depositCoin(String username, Integer coin);
    int resetDeposit(String username);
    Optional<User> saveUser(User user);
    List<User> getUsers();
    Optional<Role> saveRole(Role role);
    Optional<Role> addRoleToUser(String username, String roleName);
}
