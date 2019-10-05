package com.essheva.wordMemo.services;

import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.exceptions.InvalidCredentialsError;
import com.essheva.wordMemo.exceptions.UserAlreadyExistsError;
import com.essheva.wordMemo.exceptions.UserNotFound;

public interface UserService {

    User addUser(User user) throws UserAlreadyExistsError;

    User passwordMatch(String username, String password) throws UserNotFound, InvalidCredentialsError;

    User findUserById(String id) throws UserNotFound;

    User findUserByUsername(String username) throws UserNotFound;

    User findUserByEmail(String email) throws UserNotFound;

    void updatePassword(User user, String newPassword);

    /* Update general user info, password update should be done by {@link #updatePassword(User user, String newPassword)}*/
    User update(User user);
}
