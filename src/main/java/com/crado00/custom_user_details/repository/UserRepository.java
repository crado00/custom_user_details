package com.crado00.custom_user_details.repository;

import com.crado00.custom_user_details.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명으로 사용자 조회 (대소문자 무시)
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자명이나 이메일로 사용자 조회
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    /**
     * 사용자명 존재 여부 확인
     */
    boolean existsByUsernameIgnoreCase(String username);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 활성화된 사용자만 조회
     */
    @Query("SELECT u FROM User u WHERE u.enabled = true")
    java.util.List<User> findAllEnabledUsers();
}
