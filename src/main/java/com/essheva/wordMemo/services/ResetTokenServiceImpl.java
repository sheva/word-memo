package com.essheva.wordMemo.services;

import com.essheva.wordMemo.crypto.EncryptionUtils;
import com.essheva.wordMemo.domain.ResetToken;
import com.essheva.wordMemo.exceptions.ResetTokenNotFoundError;
import com.essheva.wordMemo.repositories.ResetTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class ResetTokenServiceImpl implements ResetTokenService {

    private final ResetTokenRepository repository;

    public ResetTokenServiceImpl(ResetTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResetToken findByUserId(String userId) {
        final Optional<ResetToken> token = repository.findByUserId(userId);
        token.orElseThrow(() -> new ResetTokenNotFoundError(String.format("Reset token not found by userId '%s'. " +
                "Maybe you already changed password.", userId)));
        log.info(String.format("Reset token found by user id [%s].", userId));
        return token.get();
    }

    @Override
    public ResetToken createToken(String userId) {
        String token = EncryptionUtils.generateToken();
        LocalDateTime expiration = LocalDateTime.now().plus(ResetToken.TOKEN_LIVENESS);

        ResetToken resetToken = new ResetToken(userId, token, expiration);
        ResetToken resetTokenSaved = repository.save(resetToken);
        log.info(String.format("ResetToken has been created '%s'.", resetTokenSaved));
        return resetTokenSaved;
    }

    @Override
    public ResetToken findByToken(String token) {
        final Optional<ResetToken> resetToken = repository.findByToken(token);
        resetToken.orElseThrow(() -> new ResetTokenNotFoundError(String.format("Reset token not found '%s'.", token)));
        ResetToken resetTokenFound = resetToken.get();
        log.info(String.format("Reset token found %s.", resetTokenFound));
        return resetTokenFound;
    }

    @Override
    public void deleteAllTokensForUser(String userId) {
        final Iterable<ResetToken> iterable = repository.findAllByUserId(userId);
        repository.deleteAll(iterable);
        log.info(String.format("Deleted all password reset tokens for user '%s'.", userId));
    }
}
