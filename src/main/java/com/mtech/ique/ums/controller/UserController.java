package com.mtech.ique.ums.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mtech.ique.ums.model.entity.User;
import com.mtech.ique.ums.service.UserManagementService;
import com.mtech.ique.ums.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.mtech.ique.ums.util.JWTUtil.USER_ID;
import static com.mtech.ique.ums.util.JWTUtil.USER_NAME;

@Controller
@CrossOrigin
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
  public ResponseEntity<Object> getUser(@AuthenticationPrincipal Jwt jwtPrincipal) {
    User user = userManagementService.findByName(jwtPrincipal.getClaimAsString(USER_NAME));
    if (null == user) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(removeSensitiveInfo(objectMapper.valueToTree(user)), HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<Object> login(@RequestBody HashMap<String, String> loginForm) {
    ObjectNode objectNode =
        userManagementService.login(loginForm.get("username"), loginForm.get("password"));
    if (null == objectNode) {
      return new ResponseEntity<>("Authentication failed!", HttpStatus.UNAUTHORIZED);
    }
    return ResponseEntity.ok(objectNode);
  }

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
  public ResponseEntity<Object> deleteUser(@AuthenticationPrincipal Jwt jwtPrincipal) {
    userManagementService.delete(jwtPrincipal.getClaim(USER_ID));
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/refresh")
  public ResponseEntity<Object> refreshToken(@AuthenticationPrincipal Jwt jwtPrincipal) {
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("token", jwtUtil.generateToken(jwtPrincipal.getClaimAsString(USER_NAME)));
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
