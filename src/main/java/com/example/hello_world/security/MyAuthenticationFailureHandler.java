package com.example.hello_world.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {


    private final MyUserDetailsService myUserDetailsService;

    public MyAuthenticationFailureHandler(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    @Autowired
    ServletContext context;

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        /*response.setHeader("Content-Type", "text/xml; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        Map<String, Object> data = new HashMap<>();
        data.put("type", "register");
        data.put("success", false);
        data.put("message", "Błędny adres email lub hasło.");

        response.getWriter().println(objectMapper.writeValueAsString(data));*/
        //String redirectUrl = request.getContextPath() + "/logowanie";

        System.out.println(exception.getMessage());

        //TODO check if credentials are valid -> then try to pas the page into verification page

        if (exception.getMessage().equalsIgnoreCase("Bad credentials")) {
            request.setAttribute("error", "Błędny adres email lub hasło!");
        }
        else if (exception.getMessage().equalsIgnoreCase("User is disabled")) {
            request.setAttribute("error", "Konto powiązane z podanym adresem email wymaga weryfikacji!");
            request.setAttribute("optionalErrorInfo", String.format("%s/weryfikacja?email=%s&loginAttempt=true", request.getContextPath(), request.getParameter("email")));
        }
        else
            request.setAttribute("error", "Nie udało się zalogować.");

        request.setAttribute("dataEmail", request.getParameter("email"));
        request.setAttribute("dataPassword", request.getParameter("password"));
        RequestDispatcher dispatcher = context.getRequestDispatcher("/logowanie");
        dispatcher.forward(request, response);
    }

}