package com.mtech.ique.ums.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mtech.ique.ums.model.entity.User;
import com.mtech.ique.ums.service.UserManagementService;
import com.mtech.ique.ums.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.mtech.ique.ums.util.JWTUtil.USER_ID;
import static com.mtech.ique.ums.util.JWTUtil.USER_NAME;

@Controller
@RequestMapping(path = "/users")
public class UserController {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  @Autowired private UserManagementService userManagementService;

  @Autowired private JWTUtil jwtUtil;

  @PostMapping
  public ResponseEntity<Object> signUp(@RequestBody User user) {
    ResponseEntity<Object> r = conflictCheck(user);
    if (null != r) {
      return r;
    }
    Map<String, Object> map = new HashMap<>();
    User createdUser = userManagementService.signup(user);
    map.put("id", createdUser.getId());
    map.put("createTime", createdUser.getCreateTime());
    return new ResponseEntity<>(map, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<Object> getUser() {
    String username =
        jwtUtil
            .verifyToken(
                String.valueOf(
                    SecurityContextHolder.getContext().getAuthentication().getCredentials()))
            .getClaim(USER_NAME)
            .asString();
    User user = userManagementService.findByName(username);
    if (null == user) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(removeSensitiveInfo(objectMapper.valueToTree(user)), HttpStatus.OK);
  }

  //  @PostMapping("/login")
  //  public ResponseEntity<Map<String, String>> login(@RequestBody HashMap<String, String>
  // loginForm) {
  //    userManagementService.login(loginForm.get("username"), loginForm.get("password"));
  //    Map<String, String> map = new HashMap<>();
  //    map.put("token", "token");
  //    map.put("userType", "userType");
  //    return new ResponseEntity<>(map, HttpStatus.OK);
  //  }

  @PostMapping("/logout")
  public ResponseEntity<Object> logout() {
    //        userManagementService.logout();
    SecurityContextHolder.clearContext();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping
  public ResponseEntity<Object> updateUserInfo(@RequestBody User user) {
    ResponseEntity<Object> r = conflictCheck(user);
    if (null != r) {
      return r;
    }
    User updatedUser = userManagementService.updateUserInfo(user);
    return new ResponseEntity<>(
        removeSensitiveInfo(objectMapper.valueToTree(updatedUser)), HttpStatus.OK);
  }

  @DeleteMapping
  public ResponseEntity<Object> deleteUser() {
    Long id =
        jwtUtil
            .verifyToken(
                String.valueOf(
                    SecurityContextHolder.getContext().getAuthentication().getCredentials()))
            .getClaim(USER_ID)
            .asLong();
    userManagementService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/refresh")
  public ResponseEntity<Object> refreshToken() {
    String newToken =
        jwtUtil.refreshToken(
            String.valueOf(
                SecurityContextHolder.getContext().getAuthentication().getCredentials()));
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("token", newToken);
    return new ResponseEntity<>(responseBody, HttpStatus.OK);
  }

  private ResponseEntity<Object> conflictCheck(User newUser) {
    User existUser = userManagementService.findByName(newUser.getUsername());
    if (null != existUser && !Objects.equals(newUser.getId(), existUser.getId())) {
      Map<String, Object> map = new HashMap<>();
      map.put(
          "message",
          String.format(
              "Username '%s' has been used, please choose another one.", newUser.getUsername()));
      return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }
    return null;
  }

  private ObjectNode removeSensitiveInfo(ObjectNode objectNode) {
    objectNode.remove("password");
    return objectNode;
  }
}
