package com.mtech.ique.ums.model.enums;

public enum UserType {
  CUSTOMER("customer"),
  MERCHANT("merchant");

  private final String type;

  UserType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return type;
  }
}
