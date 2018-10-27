package com.essheva.wordMemo.services.validators;

import org.springframework.validation.BindingResult;

public interface CaptchaValidator {

    boolean validate(String expected, String actual, BindingResult bindingResult);
}
