package com.emis.shoolservice.exception;

public class SchoolServiceFailureException extends Throwable {
  public SchoolServiceFailureException(String message) {
    super(message);
  }

  public SchoolServiceFailureException(String message, Throwable cause) {
    super(message, cause);
  }
}
