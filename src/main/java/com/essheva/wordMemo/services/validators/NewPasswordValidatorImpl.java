package com.essheva.wordMemo.services.validators;

import com.essheva.wordMemo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Arrays;

@Slf4j
@Service
public class NewPasswordValidatorImpl implements NewPasswordValidator {

    private final UserPropSelectiveValidator validator;

    public NewPasswordValidatorImpl(UserPropSelectiveValidator validator) {
        this.validator = validator;
    }

    @Override
    public void validate(User user, BindingResult bindingResult) {
        validator.validate(user, Arrays.asList("password", "passwordVerified"), bindingResult);
        if (!user.getPassword().equals(user.getPasswordVerified())) {
            log.error("Passwords do not match.");
            bindingResult.addError(new FieldError("user", "passwordVerified", "Passwords do not match."));
        }
    }
}
