package com.mtech.ique.ums.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mtech.ique.ums.model.entity.User;
import com.mtech.ique.ums.model.enums.UserStatus;
import com.mtech.ique.ums.model.enums.UserType;
import com.mtech.ique.ums.service.UserManagementService;
import com.mtech.ique.ums.util.FilterResponseUtil;
import com.mtech.ique.ums.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.mtech.ique.ums.util.JWTUtil.USER_TYPE;

@Slf4j
@Component
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final JWTUtil jwtUtil;
  private final UserManagementService ums;

  public Oauth2SuccessHandler(JWTUtil jwtUtil, UserManagementService userManagementService) {
    this.jwtUtil = jwtUtil;
    this.ums = userManagementService;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String username = (String) oAuth2User.getAttribute("name") + oAuth2User.getAttribute("email");
    User user = ums.findByName(username);
    if (null == user) {
      user = new User();
      user.setUsername(username);
      user.setPassword(username);
      user.setUserType(UserType.CUSTOMER.toString());
      user.setStatus(UserStatus.ACTIVE.toString());
      user = ums.signup(user);
    }
    String token = jwtUtil.generateToken(user);

    ObjectNode msgNode = objectMapper.createObjectNode();
    msgNode.put("token", token);
    msgNode.put(USER_TYPE, user.getUserType());
    FilterResponseUtil.ok(response, msgNode);
  }
}
