package net.alex9849.cocktailpi.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UrlForwardFilter implements Filter {

    private final String REST_API_PATTERN = "^\\/(api|websocket)\\/(.+)$";
    private final String POINT_EXCLUSION_PATTERN = "^([^.]+)$";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String requestURI = servletRequest.getRequestURI();
        String contextPath = servletRequest.getContextPath();

        if(!requestURI.equals(contextPath) && !requestURI.matches(REST_API_PATTERN) && requestURI.matches(POINT_EXCLUSION_PATTERN)) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/");
            dispatcher.forward(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

}
