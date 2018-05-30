package com.essheva.wordMemo.repositories;

import com.essheva.wordMemo.domain.Lesson;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends CrudRepository<Lesson, String> {

    Iterable<Lesson> findAllByUserId(String userId);
}
