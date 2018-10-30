package com.essheva.wordMemo.services.validators;

import com.essheva.wordMemo.model.RestoreInfo;
import org.springframework.validation.BindingResult;

public interface RestoreInfoValidator {

    boolean validate(RestoreInfo expectedInfo, RestoreInfo actualInfo, BindingResult bindingResult);
}
