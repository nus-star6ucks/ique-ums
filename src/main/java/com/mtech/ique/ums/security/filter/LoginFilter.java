package com.mtech.ique.ums.security.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mtech.ique.ums.util.FilterResponseUtil;
import com.mtech.ique.ums.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  public static final String LOGIN_URL = "/users/login";
  private final AuthenticationManager authenticationManager;
  private final JWTUtil jwtUtil;

  public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.setRequiresAuthenticationRequestMatcher(
        new AntPathRequestMatcher(LOGIN_URL, HttpMethod.POST.toString()));
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    if (!request.getMethod().equals(HttpMethod.POST.toString())) {
      throw new AuthenticationServiceException(
          "Authentication method not supported: " + request.getMethod());
    }
    try {
      JsonNode loginNode = new ObjectMapper().readTree(request.getInputStream());
      UsernamePasswordAuthenticationToken authRequest =
          new UsernamePasswordAuthenticationToken(
              loginNode.get(SPRING_SECURITY_FORM_USERNAME_KEY).asText(),
              loginNode.get(SPRING_SECURITY_FORM_PASSWORD_KEY).asText());
      // Allow subclasses to set the "details" property
      setDetails(request, authRequest);
      return authenticationManager.authenticate(authRequest);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult) {
    org.springframework.security.core.userdetails.User user = (User) authResult.getPrincipal();
    String token = jwtUtil.generateToken(user.getUsername());

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode msgNode = mapper.createObjectNode();
    msgNode.put("token", token);
    FilterResponseUtil.ok(response, msgNode);
  }

  protected void unsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
    SecurityContextHolder.clearContext();
    String msg = "Failed to process authentication request";
    log.trace(msg);
    FilterResponseUtil.unauthorized(response, msg);
  }
}
