package bitc.full502.lostandfound.controller;

import bitc.full502.lostandfound.domain.entity.UserEntity;
import bitc.full502.lostandfound.service.JpaService;
import bitc.full502.lostandfound.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final JpaService jpaService;

    // ID, NAME, EMAIL 중복 확인 (EXIST, NOT_EXIST 반환)
    @GetMapping("/check")
    public String authCheck(@RequestParam String checkType, @RequestParam String userData) throws Exception {
        return jpaService.isDuplicateUserData(checkType, userData);
    }

    // 회원가입 프로세서
    @PostMapping("/register")
    public void registerUser(@RequestBody UserEntity user) throws Exception {
        jpaService.createUser(user);
    }

    // 로그인 프로세서 (랜덤 토큰값, FAILURE 반환)
    @PostMapping("/login")
    public String login(@RequestParam String userId, @RequestParam String password, @RequestParam String role,
                        @RequestParam boolean isAutoLogin) throws Exception {
        return jpaService.loginUser(userId, password, role, isAutoLogin);
    }

    // 토큰 유효성 검사 (SUCCESS, FAILURE, NULL_OR_INVALID_FORMAT 반환)
    // 앱 최초 실행 시 로컬에 토큰이 존재하면 호출, 유효하지 않거나 자동 로그인 false 시 토큰 삭제(앱에서도 삭제 해줘야됨)
    @PostMapping("/validate")
    public String validateToken(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws Exception {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Constants.NULL_OR_INVALID_FORMAT;
        }
        String token = authorizationHeader.substring(7);
        return jpaService.validateToken(token);
    }

    // 로그아웃 프로세서 (SUCCESS, FAILURE 반환)
    // 앱에서 로그아웃 버튼 클릭 시 호출하여 데이터베이스에 토큰 삭제
    @PostMapping("/logout")
    public String logout(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws Exception {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Constants.NULL_OR_INVALID_FORMAT;
        }
        String token = authorizationHeader.substring(7);
        return jpaService.logoutUser(token);
    }

    // 비밀번호 변경 프로세서 (SUCCESS, FAILURE 반환)
    @PutMapping("/password")
    public String changePassword(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                 @RequestParam String oldPassword, @RequestParam String newPassword,
                                 @RequestParam String newPasswordConfirm) throws Exception {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authorizationHeader.substring(7);
        return jpaService.changePassword(token, oldPassword, newPassword, newPasswordConfirm) ? Constants.SUCCESS : Constants.FAILURE;
    }

    // 회원탈퇴 프로세서 (SUCCESS 반환)
    @DeleteMapping("/delete")
    public String deleteUser(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws Exception {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authorizationHeader.substring(7);
        return jpaService.deleteUser(token);
    }
}
