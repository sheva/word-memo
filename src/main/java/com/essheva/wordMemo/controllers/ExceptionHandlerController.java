package com.essheva.wordMemo.controllers;

import com.essheva.wordMemo.exceptions.InvalidCredentialsError;
import com.essheva.wordMemo.exceptions.NotFoundError;
import com.essheva.wordMemo.exceptions.UserAlreadyExistsError;
import com.essheva.wordMemo.exceptions.UserNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundError.class, UserNotFound.class})
    public ModelAndView handleNotFound(Exception exception, HttpServletRequest request) {
        log.error("Handling not found exception.");
        return createModelAndView(exception, request, "404error");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserAlreadyExistsError.class)
    public ModelAndView handleBadRequest(Exception exception, HttpServletRequest request) {
        log.error("Handling malformed data request exception.");
        return createModelAndView(exception, request, "400error");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidCredentialsError.class)
    public ModelAndView handleUnauthorizedRequest(Exception exception, HttpServletRequest request) {
        log.error("Handling lack of authentication credentials exception.");
        return createModelAndView(exception, request, "401error");
    }

    private ModelAndView createModelAndView(Exception exception, HttpServletRequest request, String viewName) {
        log.error(exception.getMessage());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(viewName);
        modelAndView.addObject("exception", exception);
        modelAndView.addObject("originURL", request.getRequestURL());

        return modelAndView;
    }
}
