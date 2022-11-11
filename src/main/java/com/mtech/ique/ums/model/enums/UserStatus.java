package com.mtech.ique.ums.model.enums;

public enum UserStatus {
  ACTIVE("active"),
  CANCELLED("cancelled");

  private final String status;

  UserStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return status;
  }
}
