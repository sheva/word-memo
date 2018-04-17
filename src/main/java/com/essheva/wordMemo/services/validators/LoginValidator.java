package com.essheva.wordMemo.services.validators;

import com.essheva.wordMemo.domain.User;
import org.springframework.validation.BindingResult;

public interface LoginValidator {

    void validate(User user, BindingResult bindingResult);
}
