package com.essheva.wordMemo.services.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Slf4j
@Service
class Helper {

    void logErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation of user data failed.");
            if (log.isDebugEnabled()) {
                bindingResult.getAllErrors().forEach(error -> {
                    log.debug(error.toString());
                });
            }
        }
    }
}
