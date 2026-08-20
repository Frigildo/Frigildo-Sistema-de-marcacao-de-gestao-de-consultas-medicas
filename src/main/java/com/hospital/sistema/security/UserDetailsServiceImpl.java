package com.hospital.sistema.security;

import java.util.List;
import org.springframework.security.core.authority
        .SimpleGrantedAuthority;
import org.springframework.security.core.userdetails
        .UserDetails;
import org.springframework.security.core.userdetails
        .UserDetailsService;
import org.springframework.security.core.userdetails
        .UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.hospital.sistema.entities.User;
import com.hospital.sistema.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl
        implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(
            String email)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "Utilizador não encontrado: "
                            + email));

        SimpleGrantedAuthority authority =
            new SimpleGrantedAuthority(
                "ROLE_" + user.getProfile().name());

        return new org.springframework.security.core
                .userdetails.User(
                    user.getEmail(),
                    user.getPasswordHash(),
                    List.of(authority)
                );
    }
}