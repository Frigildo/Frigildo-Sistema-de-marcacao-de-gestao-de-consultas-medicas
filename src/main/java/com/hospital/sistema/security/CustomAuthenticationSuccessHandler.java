package com.hospital.sistema.security;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core
        .Authentication;
import org.springframework.security.core.authority
        .SimpleGrantedAuthority;
import org.springframework.security.web.authentication
        .AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        if (hasRole(authentication,
                "ROLE_ADMINISTRADOR")) {
            response.sendRedirect("/dashboard");

        } else if (hasRole(authentication,
                "ROLE_MEDICO")) {
            response.sendRedirect("/dashboard");

        } else if (hasRole(authentication,
                "ROLE_PACIENTE")) {
            response.sendRedirect("/dashboard");

        } else {
            response.sendRedirect("/login?error");
        }
    }

    private boolean hasRole(
            Authentication authentication,
            String role) {
        return authentication.getAuthorities()
                .contains(
                    new SimpleGrantedAuthority(role));
    }
}