package com.essheva.wordMemo.repositories;

import com.essheva.wordMemo.domain.ResetToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ResetTokenRepository extends CrudRepository<ResetToken, String> {

    Optional<ResetToken> findByUserId(String userId);

    Optional<ResetToken> findByToken(String token);

    Iterable<ResetToken> findAllByUserId(String userId);
}
