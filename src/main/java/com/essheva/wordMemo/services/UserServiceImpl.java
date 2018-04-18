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
        final String salt = getSalt();
        final String passwordSecured = generateSecurePassword(user.getPassword(), salt);
        user.setSalt(salt);
        user.setPassword(passwordSecured);

        try {
            userRepository.save(user);
            log.info("User has been created: " + user);
        } catch (DuplicateKeyException e) {
            log.error("User with the same username was found.", e);
            throw new UserAlreadyExistsError(format("User with username '%s' already exists. " +
                    "Please, choose another username.", user.getUsername()), e);
        }
        return user;
    }

    @Override
    public User userLogin(final String username, final String password) {
        final Optional<User> userOptional = userRepository.findByUsername(username);
        final User user = userOptional.orElseThrow(() -> new UserNotFound(format("Can't find user '%s'.", username)));

        boolean passwordMatch = verifyUserPassword(password, user.getPassword(), user.getSalt());
        if (!passwordMatch) {
            log.error("Password does not match error.");
            throw new InvalidCredentialsError("Password mismatch error.");
        }

        log.info(format("User '%s' has been logged in.", username));
        return user;
    }

    @Override
    public Set<User> getUsers() {
        Set<User> users = new HashSet<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    @Override
    public User findUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElseThrow(() -> new UserNotFound(format("Can't find user with mail address '%s'.", email)));
        log.info(format("User with mail address '%s' found.", email));
        return user;
    }
}
