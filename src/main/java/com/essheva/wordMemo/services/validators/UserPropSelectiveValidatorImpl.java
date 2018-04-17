package com.essheva.wordMemo.services.validators;

import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.services.validators.UserPropSelectiveValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UserPropSelectiveValidatorImpl implements UserPropSelectiveValidator {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator;

    public UserPropSelectiveValidatorImpl() {
        validator = factory.getValidator();
    }

    @Override
    public void validate(User user, List<String> properties, BindingResult bindingResult) {
        final String objectName = "user";
        SpringValidatorAdapter springValidator = new SpringValidatorAdapter(validator);
        properties.parallelStream().forEach((property) -> {
            processValidationErrors(user, objectName, property, springValidator, bindingResult);
        });
    }

    @Override
    public void validate(User user, String property, BindingResult bindingResult) {
        validate(user, Arrays.asList(property), bindingResult);
    }

    private void processValidationErrors(User user, String objectName, String property,
                                         SpringValidatorAdapter springValidator, BindingResult bindingResult) {
        springValidator.validateProperty(user, property).forEach((v) -> {
            log.debug(String.format("Validation error for '%s' property: %s", objectName + "." + property, v.getMessage()));
            bindingResult.addError(new FieldError(objectName, property, v.getMessage()));
        });
    }
}

