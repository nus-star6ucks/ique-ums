package com.mtech.ique.ums.model.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
//@Entity
public class User {

    private Long id;
    private String password;
    private String userName;
    private String phoneNumber;
    private String status;
}
