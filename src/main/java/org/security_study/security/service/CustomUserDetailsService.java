package org.security_study.security.service;

import lombok.RequiredArgsConstructor;
import org.security_study.domain.UserInfo;
import org.security_study.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLoginId(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(username + "을 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(UserInfo userInfo) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userInfo.getAuthority().toString());

        return new User(
                String.valueOf(userInfo.getId()),
                userInfo.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}
