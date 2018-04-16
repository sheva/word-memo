package com.essheva.wordMemo.repositories;

import com.essheva.wordMemo.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}
