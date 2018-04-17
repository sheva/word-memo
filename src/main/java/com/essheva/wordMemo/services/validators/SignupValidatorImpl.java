package com.essheva.wordMemo.services.validators;

import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.services.validators.SignupValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


@Slf4j
@Service
public class SignupValidatorImpl implements SignupValidator {

    @Override
    public void validate(User user, BindingResult bindingResult) {
        if (!user.getPassword().equals(user.getPasswordVerified())) {
            log.error("Passwords do not match.");
            bindingResult.addError(new FieldError("user", "passwordVerified", "Passwords do not match."));
        }
    }
}
