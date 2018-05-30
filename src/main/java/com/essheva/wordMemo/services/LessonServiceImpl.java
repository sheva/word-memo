package com.essheva.wordMemo.services;

import com.essheva.wordMemo.domain.Lesson;
import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.exceptions.UserNotFound;
import com.essheva.wordMemo.repositories.LessonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;

@Slf4j
@Service
public class LessonServiceImpl implements LessonService {

    private UserService userService;
    private LessonRepository lessonRepository;

    public LessonServiceImpl(UserService userService, LessonRepository lessonRepository) {
        this.userService = userService;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public Collection<Lesson> findAllByUserId(String userId) throws UserNotFound {
        User user = userService.findUserById(userId);
        HashSet<Lesson> lessons = new HashSet<>();
        lessonRepository.findAllByUserId(user.getId()).forEach(lessons::add);
        return lessons;
    }
}
