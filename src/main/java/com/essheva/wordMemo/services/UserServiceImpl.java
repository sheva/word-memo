package com.essheva.wordMemo.services;

import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.exceptions.InvalidCredentialsError;
import com.essheva.wordMemo.exceptions.UserAlreadyExistsError;
import com.essheva.wordMemo.exceptions.UserNotFound;
import com.essheva.wordMemo.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.essheva.wordMemo.crypto.EncryptionUtils.*;
import static java.lang.String.format;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(final User user) {
        final String salt = getSalt(30);
        final String passwordSecured = generateSecurePassword(user.getPassword(), salt);

        user.setSalt(salt);
        user.setPassword(passwordSecured);

        try {
            userRepository.save(user);
            log.info("User has been created: " + user);
        } catch (DuplicateKeyException e) {
            log.error("Duplicated user found.", e);
            throw new UserAlreadyExistsError(format("User with username '%s' already exists. Please, choose another username.", user.getUsername()), e);
        }

        return user;
    }

    @Override
    public User userLogin(final String username, final String password) {
        final Optional<User> userOptional = userRepository.findById(username);
        final User user = userOptional.orElseThrow(() -> new UserNotFound(String.format("Can't find user '%s'.", username)));

        boolean passwordMatch = verifyUserPassword(password, user.getPassword(), user.getSalt());
        if (!passwordMatch) {
            log.error("Password does not match error.");
            throw new InvalidCredentialsError("Password mismatch error.");
        }

        log.info(String.format("User '%s' has been logged in.", user.getUsername()));
        return user;
    }

    @Override
    public Set<User> getUsers() {
        Set<User> users = new HashSet<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }
}
