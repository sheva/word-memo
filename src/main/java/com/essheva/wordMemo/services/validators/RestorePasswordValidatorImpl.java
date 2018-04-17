package com.essheva.wordMemo.services.validators;

import com.essheva.wordMemo.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class RestorePasswordValidatorImpl implements RestorePasswordValidator {

    private final UserPropSelectiveValidator validator;

    public RestorePasswordValidatorImpl(UserPropSelectiveValidator validator) {
        this.validator = validator;
    }

    @Override
    public void validate(User user, BindingResult bindingResult) {
        validator.validate(user, "email", bindingResult);
    }
}
