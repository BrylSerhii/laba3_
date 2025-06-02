package com.srgblr.laba3.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.FieldNameConstants;

@Value
@FieldNameConstants
@Getter
@Setter
public class ApplicationCreateDto {

    Long facultyId;

    Integer priority;
}
