package com.mtech.ique.ums.service;

import com.mtech.ique.ums.model.entity.User;

public interface UserManagementService {

  //  Boolean login(String userName, String password);

  Boolean logout(Long id);

  User findByName(String name);

  User signup(User user);

  User updateUserInfo(User user);

  void delete(Long id);
}
