package org.garden.com.service;

import org.garden.com.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

    User create(User user);

    List<User> getAll();

    User edit(long id, User user);

    User getById(long id);

    ResponseEntity<Void> delete(long id);

//    User getCurrentUser();
}
