package com.essheva.wordMemo.services.validators;

import com.essheva.wordMemo.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class UserEmailValidatorImpl implements UserEmailValidator {

    private final UserPropSelectiveValidator validator;
    private final LogHelper logHelper;

    public UserEmailValidatorImpl(UserPropSelectiveValidator validator, LogHelper logHelper) {
        this.validator = validator;
        this.logHelper = logHelper;
    }

    @Override
    public boolean validate(User user, BindingResult bindingResult) {
        validator.validate(user, "email", bindingResult);
        logHelper.logErrors(bindingResult);
        return !bindingResult.hasErrors();
    }
}
