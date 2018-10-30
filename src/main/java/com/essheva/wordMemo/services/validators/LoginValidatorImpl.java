package com.essheva.wordMemo.services.validators;

import com.essheva.wordMemo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Arrays;

@Slf4j
@Service
public class LoginValidatorImpl implements LoginValidator {

    private final PropSelectiveValidator<User> validator;
    private final Helper<User> helper;

    public LoginValidatorImpl(PropSelectiveValidator<User> validator, Helper<User> helper) {
        this.validator = validator;
        this.helper = helper;
    }

    @Override
    public boolean validate(User user, BindingResult bindingResult) {
        validator.validate(user, Arrays.asList("username", "password"), bindingResult);
        helper.logErrors(user, bindingResult);
        return !bindingResult.hasErrors();
    }
}
