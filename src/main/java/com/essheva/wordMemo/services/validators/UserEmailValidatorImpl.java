package com.essheva.wordMemo.services.validators;

import com.essheva.wordMemo.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class UserEmailValidatorImpl implements UserEmailValidator {

    private final UserPropSelectiveValidator validator;
    private final Helper helper;

    public UserEmailValidatorImpl(UserPropSelectiveValidator validator, Helper helper) {
        this.validator = validator;
        this.helper = helper;
    }

    @Override
    public boolean validate(User user, BindingResult bindingResult) {
        validator.validate(user, "email", bindingResult);
        helper.logErrors(bindingResult);
        return !bindingResult.hasErrors();
    }
}
