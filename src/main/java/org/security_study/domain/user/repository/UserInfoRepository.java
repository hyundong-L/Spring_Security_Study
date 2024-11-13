package org.security_study.domain.user.repository;

import org.security_study.domain.user.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);
    UserInfo findByRefreshToken(String refreshToken);
}
