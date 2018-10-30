package com.essheva.wordMemo.services.validators;

import com.essheva.wordMemo.model.RestoreInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


@Slf4j
@Service
public class RestoreInfoValidatorImpl implements RestoreInfoValidator {

    private final PropSelectiveValidator<RestoreInfo> validator;
    private final Helper<RestoreInfo> helper;

    public RestoreInfoValidatorImpl(PropSelectiveValidator<RestoreInfo> validator, Helper<RestoreInfo> helper) {
        this.validator = validator;
        this.helper = helper;
    }

    @Override
    public boolean validate(RestoreInfo expectedInfo, RestoreInfo actualInfo, BindingResult bindingResult) {
        if (Strings.isBlank(expectedInfo.getCaptcha()) || (!expectedInfo.getCaptcha().equals(actualInfo.getCaptcha()))) {
            log.debug("Captcha does not match.");
            bindingResult.addError(new FieldError("restoreInfo", "captcha", "Captcha does not match."));
        }
        validator.validate(actualInfo, "email", bindingResult);

        helper.logErrors(actualInfo, bindingResult);

        return !bindingResult.hasErrors();
    }
}


