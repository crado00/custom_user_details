package com.crado00.custom_user_details.service;

import com.crado00.custom_user_details.dto.UserRegistrationDto;
import com.crado00.custom_user_details.model.Role;
import com.crado00.custom_user_details.model.User;
import com.crado00.custom_user_details.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 사용자 관리 서비스
 * 회원가입, 사용자 조회, 계정 관리 등의 기능 제공
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 새 사용자 등록 (회원가입)
     */
    public User registerUser(UserRegistrationDto registrationDto) {
        log.info("👤 새 사용자 등록 시도: {}", registrationDto.getUsername());

        // 중복 확인
        validateUserUniqueness(registrationDto);

        // User Entity 생성
        User user = User.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .fullName(registrationDto.getFullName())
                .password(passwordEncoder.encode(registrationDto.getPassword())) // 패스워드 암호화
                .roles(Set.of(Role.USER)) // 기본 권한: USER
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        User savedUser = userRepository.save(user);

        log.info("✅ 사용자 등록 완료: {} (ID: {})", savedUser.getUsername(), savedUser.getId());

        return savedUser;
    }

    /**
     * 사용자명 중복 확인
     */
    private void validateUserUniqueness(UserRegistrationDto dto) {
        if (userRepository.existsByUsernameIgnoreCase(dto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + dto.getUsername());
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다: " + dto.getEmail());
        }
    }

    /**
     * 사용자 조회 (ID로)
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 사용자 조회 (사용자명으로)
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    /**
     * 모든 활성화된 사용자 조회
     */
    @Transactional(readOnly = true)
    public List<User> findAllEnabledUsers() {
        return userRepository.findAllEnabledUsers();
    }

    /**
     * 사용자 계정 상태 변경
     */
    public void updateAccountStatus(Long userId, boolean enabled, boolean accountNonLocked) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        user.setEnabled(enabled);
        user.setAccountNonLocked(accountNonLocked);

        userRepository.save(user);

        log.info("🔧 사용자 상태 변경: {} - Enabled: {}, NonLocked: {}",
                user.getUsername(), enabled, accountNonLocked);
    }

    /**
     * 사용자 권한 추가
     */
    public void addRoleToUser(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        user.addRole(role);
        userRepository.save(user);

        log.info("🛡️ 사용자 권한 추가: {} - 추가된 권한: {}", user.getUsername(), role);
    }

    /**
     * 마지막 로그인 시간 업데이트
     */
    public void updateLastLoginTime(String username) {
        userRepository.findByUsernameIgnoreCase(username)
                .ifPresent(user -> {
                    user.updateLastLoginAt();
                    userRepository.save(user);
                    log.debug("⏰ 마지막 로그인 시간 업데이트: {}", username);
                });
    }
}