package com.essheva.wordMemo.services;

import com.essheva.wordMemo.domain.ResetToken;

public interface ResetTokenService {

    ResetToken findByUserId(String userId);

    ResetToken findByToken(String token);

    ResetToken createToken(String userId);

    void deleteAllTokensForUser(String userId);
}
