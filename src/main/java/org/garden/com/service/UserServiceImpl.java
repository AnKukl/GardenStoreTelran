package org.garden.com.service;

import org.garden.com.entity.Cart;
import org.garden.com.entity.User;
import org.garden.com.enums.Role;
import org.garden.com.exceptions.UserNotFoundException;
import org.garden.com.repository.UserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserJpaRepository repository;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);


    @Override
    public User create(User user) {
        log.debug("Creating user: {}", user);
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
        user.setRole(Role.CUSTOMER);
        return repository.save(user);
    }

    @Override
    public List<User> getAll() {
        log.debug("Fetching users");
        return repository.findAll();
    }

    @Override
    public User edit(long id, User user) {
        log.debug("Editing user with ID {}: {}", id, user);
        User existingUser = repository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id " + id));

        existingUser.setName(user.getName());
        existingUser.setPhoneNumber(user.getPhoneNumber());

        User updatedUser = repository.save(existingUser);
        log.debug("User updated: {}", updatedUser);
        return updatedUser;
    }

    @Override
    public User getById(long id) {
        log.debug("Fetching user with ID: {}", id);
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        log.debug("Found user: {}", user);
        return user;
    }

    @Override
    public void delete(long id) {
        log.debug("Deleting user with ID: {}", id);
        if (repository.findById(id).isPresent()) {
            repository.deleteById(id);
            log.debug("User with ID {} deleted", id);
        } else {
            log.info("User not deleted: {}", id);
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }

//    раскомменчу после Security
//    @Override
//    public User getCurrentUser() {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        return getByLogin(userName);
//    }
}
