package com.emis.schoolservice.exception;

import java.util.concurrent.TimeoutException;

public class SchoolServiceTimeoutException extends Throwable {
  public SchoolServiceTimeoutException(String databaseTimeout, TimeoutException ex) {
    super(databaseTimeout, ex);
  }
}
