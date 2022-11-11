package com.mtech.ique.ums.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mtech.ique.ums.model.entity.User;
import com.mtech.ique.ums.repository.UserRepository;
import com.mtech.ique.ums.service.UserManagementService;
import com.mtech.ique.ums.util.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.mtech.ique.ums.util.JWTUtil.USER_TYPE;

@Service
public class UserManagementServiceImpl implements UserManagementService {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JWTUtil jwtUtil;
  private final AuthenticationManager authenticationManager;

  public UserManagementServiceImpl(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JWTUtil jwtUtil,
      AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.authenticationManager = authenticationManager;
  }

  @Override
  public ObjectNode login(String username, String password) {
    UsernamePasswordAuthenticationToken authRequest =
        new UsernamePasswordAuthenticationToken(username, password);
    Authentication authenticationResult = authenticationManager.authenticate(authRequest);
    if (null == authenticationResult) {
      return null;
    }
    User user = findByName(username);
    ObjectNode objectNode = objectMapper.createObjectNode();
    objectNode.put("token", jwtUtil.generateToken(user));
    objectNode.put(USER_TYPE, user.getUserType());
    return objectNode;
  }

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
