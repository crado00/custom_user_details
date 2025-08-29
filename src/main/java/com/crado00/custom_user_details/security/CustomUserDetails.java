package com.crado00.custom_user_details.security;

import com.crado00.custom_user_details.model.Role;
import com.crado00.custom_user_details.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security의 UserDetails 인터페이스 구현
 * User Entity를 Spring Security가 이해할 수 있는 형태로 래핑
 */
public class CustomUserDetails implements UserDetails {

    @Getter
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * 사용자 권한 목록 반환
     * Role enum을 GrantedAuthority로 변환
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = user.getRoles();
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toSet());
    }

    /**
     * 암호화된 패스워드 반환
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 사용자명 반환
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 계정 만료 여부 반환
     * true: 만료되지 않음, false: 만료됨
     */
    @Override
    public boolean isAccountNonExpired() {
        return user.getAccountNonExpired();
    }

    /**
     * 계정 잠금 여부 반환
     * true: 잠금되지 않음, false: 잠금됨
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountNonLocked();
    }

    /**
     * 자격증명(패스워드) 만료 여부 반환
     * true: 만료되지 않음, false: 만료됨
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return user.getCredentialsNonExpired();
    }

    /**
     * 계정 활성화 여부 반환
     * true: 활성화됨, false: 비활성화됨
     */
    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }

    // 편의 메서드들
    public String getEmail() {
        return user.getEmail();
    }

    public String getFullName() {
        return user.getFullName();
    }

    public Long getUserId() {
        return user.getId();
    }
}