package com.example.hello_world.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MyAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setHeader("Content-Type", "text/xml; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        Map<String, Object> data = new HashMap<>();
        data.put("type", "register");
        data.put("success", false);
        data.put("message", "Błędny adres email lub hasło.");

        response.getWriter().println(objectMapper.writeValueAsString(data));
    }

}