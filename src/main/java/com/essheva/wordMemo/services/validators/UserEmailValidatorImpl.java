package com.essheva.wordMemo.services.validators;

import com.essheva.wordMemo.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class UserEmailValidatorImpl implements UserEmailValidator {

    private final UserPropSelectiveValidator validator;

    public UserEmailValidatorImpl(UserPropSelectiveValidator validator) {
        this.validator = validator;
    }

    @Override
    public void validate(User user, BindingResult bindingResult) {
        validator.validate(user, "email", bindingResult);
    }
}
