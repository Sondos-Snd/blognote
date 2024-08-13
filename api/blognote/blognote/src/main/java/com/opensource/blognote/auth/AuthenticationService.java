package com.opensource.blognote.auth;

import com.opensource.blognote.role.RoleRepository;
import com.opensource.blognote.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private RoleRepository roleRepository;

    public void register(RegistrationRequest request) {
        var userRole = roleRepository.findByName("USER")
                // todo - better exception handling
                .orElseThrow(() -> new UsernameNotFoundException("User role not found"));
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(request.getPassword());
    }
}
