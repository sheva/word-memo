package com.essheva.wordMemo.services;

import com.essheva.wordMemo.crypto.EncryptionUtils;
import com.essheva.wordMemo.domain.ResetToken;
import com.essheva.wordMemo.exceptions.ResetTokenNotFoundError;
import com.essheva.wordMemo.exceptions.ResourceNoLongerAvailableError;
import com.essheva.wordMemo.repositories.ResetTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.lang.String.format;

@Slf4j
@Service
public class ResetTokenServiceImpl implements ResetTokenService {

    private final ResetTokenRepository repository;

    public ResetTokenServiceImpl(ResetTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResetToken createToken(String userId) {
        String token = EncryptionUtils.generateToken();
        LocalDateTime expiration = LocalDateTime.now().plus(ResetToken.TOKEN_LIVENESS);

        ResetToken resetTokenSaved = repository.save(new ResetToken(userId, token, expiration));
        if (log.isDebugEnabled()) {
            log.debug(format("ResetToken has been created '%s'.", resetTokenSaved));
        }

        return resetTokenSaved;
    }

    @Override
    public ResetToken findByToken(String token) throws ResourceNoLongerAvailableError, ResetTokenNotFoundError {
        final Optional<ResetToken> resetToken = repository.findByToken(token);
        resetToken.orElseThrow(() -> new ResetTokenNotFoundError(format("Reset token not found '%s'.", token)));

        ResetToken resetTokenFound = resetToken.get();
        if (log.isTraceEnabled()) {
            log.trace(format("Reset token found %s.", resetTokenFound));
        }

        if (LocalDateTime.now().isAfter(resetTokenFound.getExpiration())) {
            log.warn("Password reset token expired.");
            throw new ResourceNoLongerAvailableError("Password reset link has expired. Try once more.");
        }

        return resetTokenFound;
    }

    @Override
    public void deleteAllTokensForUser(String userId) {
        final Iterable<ResetToken> iterable = repository.findAllByUserId(userId);
        repository.deleteAll(iterable);
        log.debug(format("Deleted all password reset tokens for user '%s'.", userId));
    }
}
