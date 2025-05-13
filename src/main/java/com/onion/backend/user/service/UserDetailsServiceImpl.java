package com.onion.backend.user.service;

import com.onion.backend.user.domain.UserDetailsImpl;
import com.onion.backend.user.entity.UserEntity;
import com.onion.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmailOrThrow(email);

        return UserDetailsImpl.builder()
                .username(user.getUsername())
                .userId(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}

