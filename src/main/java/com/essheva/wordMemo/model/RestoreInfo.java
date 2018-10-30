package com.essheva.wordMemo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"email", "captcha"})
public class RestoreInfo {
    String email;
    String captcha;
}
