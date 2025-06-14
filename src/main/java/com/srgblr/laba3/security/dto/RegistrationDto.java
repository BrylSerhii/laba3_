package com.srgblr.laba3.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.FieldNameConstants;

@Value
@FieldNameConstants
@Getter
@Setter
public class RegistrationDto {

    String password;

    String email;

    String fullName;

    Integer mathScore;

    Integer ukrScore;

    Integer engScore;
}