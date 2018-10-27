package com.essheva.wordMemo.services;

import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.exceptions.InvalidCredentialsError;
import com.essheva.wordMemo.exceptions.UserAlreadyExistsError;
import com.essheva.wordMemo.exceptions.UserNotFound;
import com.essheva.wordMemo.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        final String salt = generateSalt();
        final String passwordSecured = generateSecurePassword(user.getPassword(), salt);
        user.setSalt(salt);
        user.setPassword(passwordSecured);

        try {
            userRepository.save(user);
            log.info("User has been created: " + user);
        } catch (DuplicateKeyException e) {
            Matcher matcher = Pattern.compile(".*index: (.*) dup key: \\{ : \"(.+)\" };.*").matcher(e.getMessage());
            if (matcher.find()) {
                String property = matcher.group(1);
                String value = matcher.group(2);

                log.warn(format("User with the same %s was found.", property), e);
                throw new UserAlreadyExistsError(property, value, e);
            } else {
                log.warn("User already exists.", e);
                throw new UserAlreadyExistsError(e.getMessage(), e);
            }
        }
        return user;
    }

    @Override
    public User passwordMatch(final String username, final String password) throws UserNotFound {
        final Optional<User> userOptional = userRepository.findByUsername(username);
        final User user = userOptional.orElseThrow(() -> new UserNotFound(format("Can't find user '%s'.", username)));

        if (!matchUserPassword(password, user.getPassword(), user.getSalt())) {
            log.warn("Password does not match error.");
            throw new InvalidCredentialsError("Password mismatch error.");
        }

        return user;
    }

    @Override
    public User findUserByEmail(String email) throws UserNotFound {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElseThrow(() -> new UserNotFound(format("Can't find user with by address '%s'.", email)));
        log.trace(format("User by email address '%s' found.", email));
        return user;
    }

    @Override
    public User findUserById(String id) throws UserNotFound {
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.orElseThrow(() -> new UserNotFound(format("Can't find user by id '%s'.", id)));
        log.trace(format("User by id '%s' found.", id));
        return user;
    }

    @Override
    public User findUserByUsername(String username) throws UserNotFound {
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.orElseThrow(() -> new UserNotFound(format("Can't find user by username '%s'.", username)));
        log.trace(format("User by username '%s' found.", username));
        return user;
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        final String salt = generateSalt();
        final String passwordSecured = generateSecurePassword(newPassword, salt);
        user.setSalt(salt);
        user.setPassword(passwordSecured);

        User userUpdated = userRepository.save(user);
        log.info(format("New password for user '%s' set.", userUpdated));
    }

    @Override
    public User update(User user) {
        User userUpdated = userRepository.save(user);
        log.info(format("User updated %s.", userUpdated));
        return userUpdated;
    }
}
