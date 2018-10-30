package com.essheva.wordMemo.services.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

@Slf4j
@Service
class Helper<T> {

    void logErrors(T obj, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn(String.format("Validation of '%s' data failed.", getObjectName(obj)));
            if (log.isDebugEnabled()) {
                bindingResult.getAllErrors().forEach(error -> {
                    log.debug(error.toString());
                });
            }
        }
    }

    String getObjectName(T obj) {
       return StringUtils.uncapitalize(obj.getClass().getSimpleName());
    }
}
