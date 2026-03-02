package com.emis.schoolservice.service;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(
"@schoolAuth.authorize(authentication, #request.schoolCode, T(com.emis.schoolservice.enums.SchoolAction).CREATE_SCHOOL)"
)
public @interface CanCreateSchool {}


