package com.essheva.wordMemo.services;

import com.essheva.wordMemo.domain.User;

import java.util.Set;

public interface UserService {

    User addUser(User user);

    User userLogin(String username, String password);

    Set<User> getUsers();

    User findUserByEmail(String email);
}
