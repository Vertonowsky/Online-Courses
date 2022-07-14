package com.example.hello_world.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MyAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // This is actually not an error, but an OK message. It is sent to avoid redirects.
        response.setHeader("Content-Type", "text/xml; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        Map<String, Object> data = new HashMap<>();
        data.put("type", "login");
        data.put("success", true);
        data.put("message", "Pomyślnie zalogowano.");

        //response.getOutputStream().println(objectMapper.writeValueAsString(data));

        response.getWriter().println(objectMapper.writeValueAsString(data));

    }
}
