package com.emis.shoolservice.exception;

public class AlreadyExistException extends RuntimeException {
  public AlreadyExistException(String msg) {
      super(msg);
  }
}
