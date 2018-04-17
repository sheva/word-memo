package com.essheva.wordMemo.services.validators;

import com.essheva.wordMemo.domain.User;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface UserPropSelectiveValidator {

    void validate(User user, List<String> properties, BindingResult bindingResult);

    void validate(User user, String property, BindingResult bindingResult);
}
