package com.kabisa.vendingmachine.service;

import com.kabisa.vendingmachine.models.Role;
import com.kabisa.vendingmachine.models.User;
import com.kabisa.vendingmachine.repositories.RoleRepository;
import com.kabisa.vendingmachine.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor @Transactional @Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductService productService;

    @Override
    public User getUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            return user;
        }
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUser(username);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public Optional<User> saveUser(User user) {
        User existentUser = userRepository.findByUsername(user.getUsername());
        log.info("Saving new user", user.getName());

        if (existentUser == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return Optional.of(userRepository.save(user));
        } else {
            log.error("Username '{}' is already taken.", user.getUsername());
            return Optional.empty();
        }
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<Role> addRoleToUser(String username, String roleName) {
        User user = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);

        if (user != null && role!= null && !user.getRole().isEquals(role)) {
            user.setRole(role);
            return Optional.of(role);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Role> saveRole(Role role) {
        Role existentRole = roleRepository.findByName(role.getName());

        if (existentRole == null) {
            log.info("Saving new role", role.getName());
            return Optional.of(roleRepository.save(role));
        } else {
            log.error("Role '{}' already exists.", role.getName());
            return Optional.empty();
        }
    }

    @Override
    public int resetDeposit(String username) {
        User user = getUser(username);

        int oldDeposit = user.getDeposit();
        user.setDeposit(0);

        return oldDeposit;
    }

    @Override
    public Optional<User> depositCoin(String username, Integer coin) {
        User user = getUser(username);
        if (productService.isCoinValid(coin)) {
            int deposit = user.getDeposit();
            user.setDeposit(deposit + coin);
            return Optional.of(user);
        }

        return Optional.empty();
    }
}
