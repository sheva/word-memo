package com.essheva.wordMemo.services.validators;

import com.essheva.wordMemo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Slf4j
@Service
public class SigninValidatorImpl implements SigninValidator {

    private final Helper<User> helper;

    public SigninValidatorImpl(Helper<User> helper) {
        this.helper = helper;
    }

    @Override
    public boolean validate(User user, BindingResult bindingResult) {
        if (!user.getPassword().equals(user.getPasswordVerified())) {
            log.debug("Passwords do not match.");
            bindingResult.addError(new FieldError("user", "passwordVerified", "Passwords do not match."));
        }
        helper.logErrors(user, bindingResult);
        return !bindingResult.hasErrors();
    }
}
