package com.essheva.wordMemo.services;

import com.essheva.wordMemo.domain.Session;
import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.exceptions.UserNotFound;

public interface SessionService {

    String getUsernameBySessionId(String id) throws UserNotFound;

    String getUserIdBySessionId(String id) throws UserNotFound;

    Session startSession(User user);

    void endSessionById(String id);
}
