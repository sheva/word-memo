package com.essheva.wordMemo.services.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@Slf4j
@Service
public class CaptchaValidatorImpl implements CaptchaValidator {

    private final LogHelper logHelper;

    public CaptchaValidatorImpl(LogHelper logHelper) {
        this.logHelper = logHelper;
    }

    @Override
    public boolean validate(String expected, String actual, BindingResult bindingResult) {
        if (!expected.equals(actual)) {
            log.debug("Captcha does not match.");
            bindingResult.addError(new ObjectError("captcha", "Captcha does not match."));
        }
        logHelper.logErrors(bindingResult);
        return !bindingResult.hasErrors();
    }
}
