package com.emis.shoolservice.exception;

import lombok.Getter;

@Getter
public class SchoolNotFoundException extends RuntimeException {
  public SchoolNotFoundException(String message) {
    super(message);
        }
}