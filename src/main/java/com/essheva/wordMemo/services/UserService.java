package com.essheva.wordMemo.services;

import com.essheva.wordMemo.domain.User;

public interface UserService {

    User addUser(User user);

    User userLogin(String username, String password);

    User findUserByEmail(String email);

    User findUserById(String id);

    void updatePassword(User user, String newPassword);
}
