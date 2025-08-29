package com.crado00.custom_user_details.service;

import com.crado00.custom_user_details.model.User;
import com.crado00.custom_user_details.repository.UserRepository;
import com.crado00.custom_user_details.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security에서 사용자 정보를 로드하는 서비스
 * 데이터베이스에서 사용자 정보를 조회하여 UserDetails로 변환
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 사용자명(또는 이메일)으로 사용자 정보를 로드
     *
     * @param username 사용자명 또는 이메일
     * @return UserDetails 구현체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("🔍 사용자 조회 시도: {}", username);

        // 사용자명이나 이메일로 사용자 조회
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> {
                    log.warn("❌ 사용자를 찾을 수 없음: {}", username);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
                });

        log.debug("✅ 사용자 조회 성공: {} (ID: {})", user.getUsername(), user.getId());

        // User Entity를 CustomUserDetails로 래핑하여 반환
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // 로그로 사용자 상태 확인
        logUserStatus(user);

        return userDetails;
    }

    /**
     * 사용자 상태를 로그로 출력 (디버깅용)
     */
    private void logUserStatus(User user) {
        log.debug("📊 사용자 상태 확인:");
        log.debug("  - Username: {}", user.getUsername());
        log.debug("  - Email: {}", user.getEmail());
        log.debug("  - Enabled: {}", user.getEnabled());
        log.debug("  - AccountNonExpired: {}", user.getAccountNonExpired());
        log.debug("  - AccountNonLocked: {}", user.getAccountNonLocked());
        log.debug("  - CredentialsNonExpired: {}", user.getCredentialsNonExpired());
        log.debug("  - Roles: {}", user.getRoles());
    }
}