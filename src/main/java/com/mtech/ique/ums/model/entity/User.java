package com.mtech.ique.ums.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Setter
@Getter
@Entity(name = "Usr")
@EntityListeners(AuditingEntityListener.class)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  private String userType;
  private String phoneNumber;
  private String status;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Long createTime;
}
