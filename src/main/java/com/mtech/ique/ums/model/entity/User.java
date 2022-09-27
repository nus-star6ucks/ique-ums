package com.mtech.ique.ums.model.entity;

import com.mtech.ique.ums.model.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Setter
@Getter
@Entity
public class User {

    private Long id;
    private String password;
    private String name;
    private String phoneNumber;
    private String status;
}
