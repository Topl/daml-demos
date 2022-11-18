package co.topl.latticedamldemo.security;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            org.springframework.security.access.AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream().map(x -> x.getAuthority()).collect(Collectors.toSet()).contains("ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/admin/accessDenied");
        } else {
            response.sendRedirect(request.getContextPath() + "/home/accessDenied");
        }

    }
}
