package com.mtech.ique.ums.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mtech.ique.ums.config.JWTConfig;
import com.mtech.ique.ums.model.entity.User;
import com.mtech.ique.ums.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class JWTUtil {

  public static final String USER_ID = "userId";
  public static final String USER_NAME = "username";
  public static final String USER_TYPE = "userType";
  @Autowired private JWTConfig jwtConfig;
  @Autowired private UserRepository userRepository;

  public String generateToken(User user) {
    String token = null;
    try {
      Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getKey());
      Map<String, Object> payloadClaims = new HashMap<>();
      payloadClaims.put(USER_ID, user.getId());
      payloadClaims.put(USER_NAME, user.getUsername());
      payloadClaims.put(USER_TYPE, user.getUserType());
      token =
          JWT.create()
              .withIssuer(jwtConfig.getIss())
              .withSubject(jwtConfig.getSub())
              .withExpiresAt(Instant.now().plus(jwtConfig.getDuration(), ChronoUnit.MINUTES))
              .withIssuedAt(Instant.now())
              .withJWTId(UUID.randomUUID().toString())
              .withPayload(payloadClaims)
              .sign(algorithm);
    } catch (JWTCreationException exception) {
      // Invalid Signing configuration / Couldn't convert Claims.
      log.error("JWTCreationException: " + exception);
    }
    return token;
  }

  public String generateToken(String username) {
    return generateToken(userRepository.findByUsername(username));
  }

  public DecodedJWT verifyToken(String token) throws JWTVerificationException {

    Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getKey()); // use more secure key
    JWTVerifier verifier =
        JWT.require(algorithm)
            .withIssuer(jwtConfig.getIss())
            .withSubject(jwtConfig.getSub())
            .build();
    return verifier.verify(token);
  }

  public String refreshToken(String oldToken) {
    return generateToken(JWT.decode(oldToken).getClaim(USER_NAME).asString());
  }
}
