package com.essheva.wordMemo.controllers;

import com.essheva.wordMemo.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
@ComponentScan(basePackages = {"com.essheva.wordMemo.controllers"})
public class ExceptionHandlerController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundError.class, UserNotFound.class, ResetTokenNotFoundError.class})
    public ModelAndView handleNotFound(Exception exception, HttpServletRequest request) {
        log.trace("Handling not found exception.");
        return createModelAndView(exception, request, "404error");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserAlreadyExistsError.class)
    public ModelAndView handleBadRequest(Exception exception, HttpServletRequest request) {
        log.trace("Handling malformed data request exception.");
        return createModelAndView(exception, request, "400error");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidCredentialsError.class)
    public ModelAndView handleUnauthorizedRequest(Exception exception, HttpServletRequest request) {
        log.trace("Handling lack of authentication credentials exception.");
        return createModelAndView(exception, request, "401error");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerError.class)
    public ModelAndView handleInternalServerError(Exception exception, HttpServletRequest request) {
        log.trace("Handling application failure error.");
        return createModelAndView(exception, request, "500error");
    }

    @ResponseStatus(HttpStatus.GONE)
    @ExceptionHandler(ResourceNoLongerAvailableError.class)
    public ModelAndView handleResourceGoneError(Exception exception, HttpServletRequest request) {
        log.trace("Handling resource no longer available error.");
        return createModelAndView(exception, request, "410error");
    }

    private ModelAndView createModelAndView(Exception exception, HttpServletRequest request, String viewName) {
        log.warn(exception.getMessage(), exception);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(viewName);
        modelAndView.addObject("exception", exception);
        modelAndView.addObject("originURL", request.getRequestURL());

        return modelAndView;
    }
}
