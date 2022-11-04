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
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
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
  public static final String SCOPE = "scope";
  private final JWTConfig jwtConfig;
  private final UserRepository userRepository;

  public JWTUtil(JWTConfig jwtConfig, UserRepository userRepository) {
    this.jwtConfig = jwtConfig;
    this.userRepository = userRepository;
  }

  public String generateToken(User user) {
    String token = null;
    Algorithm algorithm =
        Algorithm.RSA256(
            readPublicKey(new File(jwtConfig.getPublicKey())),
            readPrivateKey(new File(jwtConfig.getPrivateKey())));
    Map<String, Object> payloadClaims = new HashMap<>();
    payloadClaims.put(USER_ID, user.getId());
    payloadClaims.put(USER_NAME, user.getUsername());
    payloadClaims.put(SCOPE, user.getUserType());
    try {
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

    Algorithm algorithm =
        Algorithm.RSA256(
            readPublicKey(new File(jwtConfig.getPublicKey())),
            readPrivateKey(new File(jwtConfig.getPrivateKey())));
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

  public RSAPublicKey readPublicKey(File file) {
    try (FileReader keyReader = new FileReader(file)) {
      PEMParser pemParser = new PEMParser(keyReader);
      JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
      SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());
      return (RSAPublicKey) converter.getPublicKey(publicKeyInfo);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public RSAPrivateKey readPrivateKey(File file) {
    try (FileReader keyReader = new FileReader(file)) {

      PEMParser pemParser = new PEMParser(keyReader);
      JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
      PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());

      return (RSAPrivateKey) converter.getPrivateKey(privateKeyInfo);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
