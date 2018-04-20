package com.essheva.wordMemo.services;

import com.essheva.wordMemo.domain.Session;
import com.essheva.wordMemo.domain.User;

public interface SessionService {

    String getUsernameBySessionId(String id);

    Session startSession(User user);

    void endSessionById(String id);
}
