package com.hospital.sistema.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet
        .HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.hospital.sistema.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import java.security.Principal;

@Component
@RequiredArgsConstructor
public class UserRoleInterceptor
        implements HandlerInterceptor {

    private final UserRepository userRepository;

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) {

        if (modelAndView == null) return;

        Principal principal =
            request.getUserPrincipal();
        if (principal == null) return;

        userRepository
            .findByEmail(principal.getName())
            .ifPresent(user ->
                modelAndView.addObject(
                    "userRole",
                    user.getProfile().name()));
    }
}