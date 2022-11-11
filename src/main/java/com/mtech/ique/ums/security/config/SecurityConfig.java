package com.mtech.ique.ums.security.config;

import com.mtech.ique.ums.security.handler.Oauth2FailureHandler;
import com.mtech.ique.ums.security.handler.Oauth2SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  private final Oauth2SuccessHandler oauth2SuccessHandler;
  private final Oauth2FailureHandler oauth2FailureHandler;

  public SecurityConfig(
      @Lazy Oauth2SuccessHandler oauth2SuccessHandler, Oauth2FailureHandler oauth2FailureHandler) {
    this.oauth2SuccessHandler = oauth2SuccessHandler;
    this.oauth2FailureHandler = oauth2FailureHandler;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers("/users/login")
        .anonymous()
        .antMatchers(HttpMethod.POST, "/users")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .oauth2Login()
        .successHandler(oauth2SuccessHandler)
        .failureHandler(oauth2FailureHandler)
        .and()
        .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
