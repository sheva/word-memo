package com.essheva.wordMemo.services;

import com.essheva.wordMemo.crypto.EncryptionUtils;
import com.essheva.wordMemo.domain.Session;
import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.exceptions.NotFoundError;
import com.essheva.wordMemo.exceptions.UserNotFound;
import com.essheva.wordMemo.repositories.SessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;

@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final UserService userService;

    public SessionServiceImpl(SessionRepository sessionRepository, UserService userService) {
        this.sessionRepository = sessionRepository;
        this.userService = userService;
    }

    @Override
    public String getUsernameBySessionId(final String sessionId) throws UserNotFound {
        String userId = findUserIdBySessionId(sessionId);
        User user = userService.findUserById(userId);
        return user.getUsername();
    }

    @Override
    public String getUserIdBySessionId(final String sessionId) throws UserNotFound {
        return findUserIdBySessionId(sessionId);
    }

    @Override
    public Session startSession(final User user) {
        Session session = sessionRepository.save(new Session(EncryptionUtils.generateId(), user.getId()));
        if (log.isDebugEnabled()) {
            log.debug("Session has been started " + session);
        }
        return session;
    }

    @Override
    public void endSessionById(final String id) {
        sessionRepository.deleteById(id);
        log.debug(format("Session [%s] was deleted.", id));
    }

    private String findUserIdBySessionId(String sessionId) throws UserNotFound {
        final Optional<Session> session = sessionRepository.findById(sessionId);
        session.orElseThrow(() -> new NotFoundError("Session not found by id " + sessionId));
        log.trace(format("User found by session id [%s].", sessionId));
        return session.get().getUserId();
    }
}
