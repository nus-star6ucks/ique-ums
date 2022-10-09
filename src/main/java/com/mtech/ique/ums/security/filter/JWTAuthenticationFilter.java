package com.mtech.ique.ums.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mtech.ique.ums.util.FilterResponseUtil;
import com.mtech.ique.ums.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Slf4j
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

  private final JWTUtil jwtUtil;

  public JWTAuthenticationFilter(AuthenticationManager authManager, JWTUtil jwtUtil) {
    super(authManager);
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasLength(bearerToken)) {
      try {
        DecodedJWT jwt = jwtUtil.verifyToken(bearerToken.replace("Bearer ", "").trim());
        SecurityContextHolder.getContext()
            .setAuthentication(
                new UsernamePasswordAuthenticationToken(
                    jwt.getClaim("username"), jwt.getToken(), Collections.emptyList()));
        chain.doFilter(request, response);
      } catch (JWTVerificationException jwtVerificationException) {
        log.error(jwtVerificationException.getLocalizedMessage());
        FilterResponseUtil.forbidden(response, jwtVerificationException.getLocalizedMessage());
      }
    } else {
      log.error("Empty Token!");
      FilterResponseUtil.unauthorized(response, "Empty Token!");
    }
  }
}
