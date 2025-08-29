package com.crado00.custom_user_details.controller;

import com.crado00.custom_user_details.dto.UserRegistrationDto;
import com.crado00.custom_user_details.model.User;
import com.crado00.custom_user_details.security.CustomUserDetails;
import com.crado00.custom_user_details.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final UserService userService;

    /**
     * 홈 페이지
     */
    /*
    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("authorities", authentication.getAuthorities());
        }
        return "home";
    }

     */
    @GetMapping("/")
    public String home() {
        return "<h1>Home Page Works!</h1>";
    }

    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 회원가입 페이지
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "register";
    }

    /**
     * 회원가입 처리
     */
    @PostMapping("/register")
    public String registerUser(@Valid UserRegistrationDto registrationDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            User user = userService.registerUser(registrationDto);
            log.info("✅ 새 사용자 등록 성공: {}", user.getUsername());

            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            log.warn("❌ 회원가입 실패: {}", e.getMessage());
            bindingResult.rejectValue("username", "error.userRegistrationDto", e.getMessage());
            return "register";
        }
    }

    /**
     * 대시보드 (로그인 후 메인 페이지)
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 사용자 정보를 모델에 추가
        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("email", userDetails.getEmail());
        model.addAttribute("fullName", userDetails.getFullName());
        model.addAttribute("authorities", userDetails.getAuthorities());

        // 마지막 로그인 시간 업데이트
        userService.updateLastLoginTime(userDetails.getUsername());

        return "dashboard";
    }

    /**
     * 프로필 페이지
     */
    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("user", userDetails.getUser());
        return "profile";
    }

    /**
     * 관리자 페이지 (ADMIN 권한 필요)
     */
    @GetMapping("/admin")
    public String admin(Model model) {
        List<User> users = userService.findAllEnabledUsers();
        model.addAttribute("users", users);
        return "admin";
    }

    /**
     * 매니저 페이지 (MANAGER 또는 ADMIN 권한 필요)
     */
    @GetMapping("/manager")
    public String manager(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("user", userDetails.getUser());
        return "manager";
    }
}
