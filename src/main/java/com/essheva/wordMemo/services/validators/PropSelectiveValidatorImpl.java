package com.essheva.wordMemo.services.validators;

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
public class PropSelectiveValidatorImpl<T> implements PropSelectiveValidator<T> {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator;
    private final Helper<T> helper;

    public PropSelectiveValidatorImpl(Helper<T> helper) {
        validator = factory.getValidator();
        this.helper = helper;
    }

    private void processValidationErrors(T user, String objectName, String property,
                                         SpringValidatorAdapter springValidator, BindingResult bindingResult) {
        springValidator.validateProperty(user, property).forEach((v) -> {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Validation error for '%s' property: %s", objectName + "." + property, v.getMessage()));
            }
            bindingResult.addError(new FieldError(objectName, property, v.getMessage()));
        });
    }

    @Override
    public void validate(T obj, List<String> properties, BindingResult bindingResult) {
        SpringValidatorAdapter springValidator = new SpringValidatorAdapter(validator);
        properties.parallelStream().forEach((property) -> {
            processValidationErrors(obj, helper.getObjectName(obj), property, springValidator, bindingResult);
        });
    }

    @Override
    public void validate(T obj, String property, BindingResult bindingResult) {
        validate(obj, Arrays.asList(property), bindingResult);
    }
}
