package com.essheva.wordMemo.services;

import com.essheva.wordMemo.domain.Lesson;
import com.essheva.wordMemo.exceptions.UserNotFound;

import java.util.Collection;

public interface LessonService {

    Collection<Lesson> findAllByUserId(String userId) throws UserNotFound;
}
