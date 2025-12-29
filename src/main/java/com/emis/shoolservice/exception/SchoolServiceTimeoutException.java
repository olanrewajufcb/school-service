package com.emis.shoolservice.exception;

import java.util.concurrent.TimeoutException;

public class SchoolServiceTimeoutException extends Throwable {
  public SchoolServiceTimeoutException(String databaseTimeout, TimeoutException ex) {
    super(databaseTimeout, ex);
  }
}
