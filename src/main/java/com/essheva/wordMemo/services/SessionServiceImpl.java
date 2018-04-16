package com.essheva.wordMemo.services;

import com.essheva.wordMemo.crypto.EncryptionUtils;
import com.essheva.wordMemo.domain.Session;
import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.exceptions.SessionNotFound;
import com.essheva.wordMemo.repositories.SessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import static java.lang.String.format;

@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    public SessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public String getUsernameBySessionId(final String id) {
        final Optional<Session> session = sessionRepository.findById(id);
        session.orElseThrow(() -> {
            log.error("Session not found by id " + id);
            throw new SessionNotFound("Session not found by id " + id);
        });
        log.info(format("User found by session id [%s].", id));
        return session.get().getUsername();
    }

    @Override
    public Session startSession(final User user) {
        Session session = sessionRepository.save(new Session(EncryptionUtils.generateId(), user.getUsername()));
        log.info("Session has been started " + session.toString());
        return session;
    }

    @Override
    public void endSessionById(final String id) {
        log.info("Deleting session by id [%s]." + id);
        sessionRepository.deleteById(id);
    }

    @Override
    public Session findSessionById(final String id) {
        final Optional<Session> session = sessionRepository.findById(id);
        session.orElseThrow(() -> {
            log.error(format("Session not found by id [%s].", id));
            throw new SessionNotFound("Session not found by id " + id);
        });
        log.info("Session found " + session.toString());
        return session.get();
    }
}
