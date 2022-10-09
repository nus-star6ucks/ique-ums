package com.mtech.ique.ums.service.impl;

import com.mtech.ique.ums.model.entity.User;
import com.mtech.ique.ums.repository.UserRepository;
import com.mtech.ique.ums.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserManagementServiceImpl implements UserManagementService {

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  //  @Override
  //  public Boolean login(String userName, String password) {
  //
  //    return passwordEncoder.matches(password,
  // userRepository.findByUsername(userName).getPassword());
  //  }

  @Override
  public Boolean logout(Long id) {
    return null;
  }

  @Override
  public User findByName(String name) {
    return userRepository.findByUsername(name);
  }

  @Override
  public User signup(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  @Override
  public User updateUserInfo(User user) {

    if (StringUtils.hasLength(user.getPassword())) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
    } else {
      user.setPassword(findByName(user.getUsername()).getPassword());
    }
    return userRepository.save(user);
  }

  @Override
  public void delete(Long id) {
    userRepository.deleteById(id);
  }
}
