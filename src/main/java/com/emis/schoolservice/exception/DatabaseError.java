package com.emis.schoolservice.exception;

public class DatabaseError extends  RuntimeException {
  public DatabaseError(String msg) {
      super(msg);
  }
}
