package com.essheva.wordMemo.services.validators;

import com.essheva.wordMemo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Arrays;

@Slf4j
@Service
public class LoginValidatorImpl implements LoginValidator {

    private final UserPropSelectiveValidator validator;

    public LoginValidatorImpl(UserPropSelectiveValidator validator) {
        this.validator = validator;
    }

    @Override
    public void validate(User user, BindingResult bindingResult) {
        validator.validate(user, Arrays.asList("username", "password"), bindingResult);
    }
}
