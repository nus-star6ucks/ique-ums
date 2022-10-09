package com.mtech.ique.ums.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FilterResponseUtil {
  private static final ObjectMapper mapper = new ObjectMapper();

  public static void out(HttpServletResponse response, ObjectNode msgNode) {
    try {
      mapper.writeValue(response.getWriter(), msgNode);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void ok(HttpServletResponse response, ObjectNode msgNode) {
    response.setStatus(HttpStatus.OK.value());
    out(response, msgNode);
  }

  public static void unauthorized(HttpServletResponse response, String msg) {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    ObjectNode msgNode = mapper.createObjectNode();
    msgNode.put("message", msg);
    out(response, msgNode);
  }

  public static void forbidden(HttpServletResponse response, String msg) {
    response.setStatus(HttpStatus.FORBIDDEN.value());
    ObjectNode msgNode = mapper.createObjectNode();
    msgNode.put("message", msg);
    out(response, msgNode);
  }
}
