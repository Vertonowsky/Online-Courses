package com.example.vertonowsky.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {


    /**
     * Renders page with proper error message
     *
     * @param request stands for rqeust made by user
     * @param model instance of the Model class. Used to pass attributes to the end user
     * @return HTML page with error information
     */
    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE); // get error status

        String title       = "Bad Request";
        int statusCode     = 400;
        String description = "Strona, która próbujesz odwiedzić jest tymczasowo niedostępna.";

        if (status != null) {
            statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                title       = "Page not found";
                description = "Wygląda na to, że strona, której szukasz nie istnieje.";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                title       = "Internal server error";
                description = "Serwer nie mógł zrealizować Twojego żądania. Prosimy o kontakt jeśli ponownie natrafisz na ten komunikat.";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                title       = "Permission denied";
                description = "Wygląda na to, że nie masz dostępu do strony, którą próbujesz odwiedzić.";
            }
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("title", title);
        model.addAttribute("description",description);
        return new ModelAndView("error");
    }
}
