package com.crado00.custom_user_details.config;

import com.crado00.custom_user_details.model.Role;
import com.crado00.custom_user_details.model.User;
import com.crado00.custom_user_details.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 애플리케이션 시작 시 테스트용 사용자 데이터를 초기화
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {
            log.info("🚀 테스트용 사용자 데이터 초기화 시작");

            createTestUsers();

            log.info("✅ 테스트용 사용자 데이터 초기화 완료");
            log.info("📊 총 사용자 수: {}", userRepository.count());
        } else {
            log.info("📋 기존 사용자 데이터 존재, 초기화 건너뜀");
        }
    }

    private void createTestUsers() {
        // 관리자 계정
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@example.com")
                .fullName("관리자")
                .roles(Set.of(Role.ADMIN, Role.MANAGER, Role.USER))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        userRepository.save(admin);
        log.info("👨‍💻 관리자 계정 생성: {}", admin.getUsername());

        // 매니저 계정
        User manager = User.builder()
                .username("manager")
                .password(passwordEncoder.encode("manager123"))
                .email("manager@example.com")
                .fullName("매니저")
                .roles(Set.of(Role.MANAGER, Role.USER))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        userRepository.save(manager);
        log.info("👨‍💼 매니저 계정 생성: {}", manager.getUsername());

        // 일반 사용자 계정
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user123"))
                .email("user@example.com")
                .fullName("일반 사용자")
                .roles(Set.of(Role.USER))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        userRepository.save(user);
        log.info("👤 일반 사용자 계정 생성: {}", user.getUsername());

        // 비활성화된 계정 (테스트용)
        User disabledUser = User.builder()
                .username("disabled")
                .password(passwordEncoder.encode("disabled123"))
                .email("disabled@example.com")
                .fullName("비활성화된 사용자")
                .roles(Set.of(Role.USER))
                .enabled(false) // 비활성화
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        userRepository.save(disabledUser);
        log.info("❌ 비활성화 계정 생성: {}", disabledUser.getUsername());

        // 잠긴 계정 (테스트용)
        User lockedUser = User.builder()
                .username("locked")
                .password(passwordEncoder.encode("locked123"))
                .email("locked@example.com")
                .fullName("잠긴 사용자")
                .roles(Set.of(Role.USER))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(false) // 잠김
                .credentialsNonExpired(true)
                .build();

        userRepository.save(lockedUser);
        log.info("🔒 잠긴 계정 생성: {}", lockedUser.getUsername());
    }
}