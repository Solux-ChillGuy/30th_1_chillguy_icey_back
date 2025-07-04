package com.project.icey.app.repository;


import com.project.icey.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 이메일 값을 기준으로 User 엔티티 조회.
     */
    Optional<User> findByEmail(String email);
     /**
     * 사용자 Rtoken 기준으로 User 엔티티 조회.
     */
    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> getUserById(Long id);
}
