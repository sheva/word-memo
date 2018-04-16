package com.essheva.wordMemo.repositories;

import com.essheva.wordMemo.domain.Session;
import org.springframework.data.repository.CrudRepository;

public interface SessionRepository extends CrudRepository<Session, String> {
}
