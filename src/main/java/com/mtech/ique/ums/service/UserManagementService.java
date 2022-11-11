package com.mtech.ique.ums.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mtech.ique.ums.model.entity.User;

public interface UserManagementService {

  ObjectNode login(String username, String password);

  Boolean logout(Long id);

  User findByName(String name);

  User signup(User user);

  User updateUserInfo(User user);

  void delete(Long id);
}
