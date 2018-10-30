package com.essheva.wordMemo.services.validators;

import org.springframework.validation.BindingResult;

import java.util.List;


public interface PropSelectiveValidator<T> {

    void validate(T obj, List<String> properties, BindingResult bindingResult);

    void validate(T obj, String property, BindingResult bindingResult);
}
