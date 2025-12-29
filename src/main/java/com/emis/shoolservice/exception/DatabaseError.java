package com.emis.shoolservice.exception;

public class DatabaseError extends  RuntimeException {
  public DatabaseError(String msg) {
      super(msg);
  }
}
