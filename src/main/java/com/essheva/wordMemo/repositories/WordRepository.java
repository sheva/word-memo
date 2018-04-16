package com.essheva.wordMemo.repositories;

import com.essheva.wordMemo.domain.Word;
import org.springframework.data.repository.CrudRepository;

public interface WordRepository extends CrudRepository<Word, String> {
}
