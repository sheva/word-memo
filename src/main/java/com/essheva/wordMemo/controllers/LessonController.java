package com.essheva.wordMemo.controllers;

import com.essheva.wordMemo.exceptions.UserNotFound;
import com.essheva.wordMemo.services.LessonService;
import com.essheva.wordMemo.services.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@ComponentScan(basePackages = {"com.essheva.wordMemo.controllers"})
public class LessonController {

    private final SessionService sessionService;
    private final LessonService lessonService;

    public LessonController(SessionService sessionService, LessonService lessonService) {
        this.sessionService = sessionService;
        this.lessonService = lessonService;
    }

    // TODO: implement better authentication mechanism

    @GetMapping({"/lessons"})
    public String getAllLessonsForUser(@CookieValue("sessionId") String sessionId, Model model) {
        try {
            String userId = sessionService.getUserIdBySessionId(sessionId);
            lessonService.findAllByUserId(userId);
            // TODO: process result
        } catch (UserNotFound e) {
            log.warn("User has been deleted. Perform logout.");
            return "redirect:/logout";
        }
        return "lessons";
    }

    @PostMapping({"/lessons"})
    public String addNewLessonForUser(@CookieValue("sessionId") String sessionId) {
        // TODO: implement
        throw new UnsupportedOperationException("TODO: implement");
    }

    @PostMapping({"/lessons/{lessonId}/words"})
    public String addNewWordToLesson(@PathVariable String lessonId, @CookieValue("sessionId") String sessionId) {
        // TODO: implement
        throw new UnsupportedOperationException("TODO: implement");
    }

    @PutMapping({"/lessons/{lessonId}/words/{wordId}"})
    public String modifyWordInLesson(@PathVariable String lessonId, @PathVariable String wordId,
                                     @CookieValue("sessionId") String sessionId) {
        // TODO: implement
        throw new UnsupportedOperationException("TODO: implement");
    }

    @DeleteMapping({"/lessons/{lessonId}/words/{wordId}"})
    public String deleteWordInLesson(@PathVariable String lessonId, @PathVariable String wordId,
                                     @CookieValue("sessionId") String sessionId) {
        // TODO: implement
        throw new UnsupportedOperationException("TODO: implement");
    }
}
