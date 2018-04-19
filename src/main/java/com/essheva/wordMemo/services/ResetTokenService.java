package com.essheva.wordMemo.services;

import com.essheva.wordMemo.domain.ResetToken;
import com.essheva.wordMemo.exceptions.ResetTokenNotFoundError;
import com.essheva.wordMemo.exceptions.ResourceNoLongerAvailableError;

public interface ResetTokenService {

    ResetToken findByToken(String token) throws ResourceNoLongerAvailableError, ResetTokenNotFoundError;

    ResetToken createToken(String userId);

    void deleteAllTokensForUser(String userId);
}
