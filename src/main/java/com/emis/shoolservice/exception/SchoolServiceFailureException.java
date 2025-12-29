package com.emis.shoolservice.exception;

public class SchoolServiceFailureException extends Throwable {
  public SchoolServiceFailureException(String failedToFetchStudents, Throwable error) {
    super(failedToFetchStudents, error);
  }
}
