package bitc.full502.projectbq.controller;

import bitc.full502.projectbq.common.Constants;
import bitc.full502.projectbq.common.response.JsonResponse;
import bitc.full502.projectbq.dto.*;
import bitc.full502.projectbq.service.UserService;
import bitc.full502.projectbq.util.HeaderUtil;
import bitc.full502.projectbq.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // 로그인 프로세스(사원코드, 비밀번호 필요)
    @PostMapping("/auth/login")
    public ResponseEntity<JsonResponse> login(@Valid @RequestBody AuthDto authDto, boolean autoLogin) {
        return ResponseEntity.ok(new JsonResponse(Constants.SUCCESS, userService.loginUser(authDto, autoLogin)));
    }

    // 웹 용 로그인 프로세스
    @PostMapping("/auth/web-login")
    public ResponseEntity<JsonResponse> login(@Valid @RequestBody AuthDto authDto, boolean autoLogin,
                                              HttpServletResponse response) {
        String token = userService.loginUser(authDto, autoLogin);

        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        if (autoLogin) cookie.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(cookie);
        return ResponseEntity.ok(new JsonResponse(Constants.SUCCESS));
    }

    @PostMapping("/auth/web-logout")
    public ResponseEntity<JsonResponse> logout(HttpServletResponse response) {
        // 기존 토큰 쿠키 삭제
        Cookie cookie = new Cookie("token", null); // 값 비우기
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JsonResponse(Constants.SUCCESS));
    }

    // 자동 로그인 프로세스(헤더 토큰 필요)
    @PostMapping("/auth/auto-login")
    public ResponseEntity<JsonResponse> autoLogin(@CookieValue(value = "token", required = false) String webToken,
                                                  @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        JwtUtil.validateToken(token);
        return ResponseEntity.ok(new JsonResponse(Constants.SUCCESS));
    }

    // 사용자 등록 프로세스(사원코드, 비밀번호 필요)
    @PostMapping("/users")
    public ResponseEntity<JsonResponse> join(@Valid @RequestBody AuthDto authDto) {
        userService.joinUser(authDto);
        return ResponseEntity.ok(new JsonResponse(Constants.SUCCESS));
    }

    // 전체 사용자 정보 조회(필터, 헤더 토큰 필요)
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUserByFilter(@CookieValue(value = "token", required = false) String webToken,
                                                            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                            UserSearchDto userFilter) {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(userEmpCode, Constants.PERMISSION_UPDATE_USER_INFO);
        return ResponseEntity.ok(userService.getAllUserByFilter(userFilter));
    }

    // 내 정보 조회(헤더 토큰 필요)
    @GetMapping("/users/me")
    public ResponseEntity<UserDto> getMyInfo(@CookieValue(value = "token", required = false) String webToken,
                                             @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        return ResponseEntity.ok(userService.getUserByEmpCode(userEmpCode));
    }

    // 내 비밀번호 변경
    @PutMapping("/users/me/pwd")
    public ResponseEntity<JsonResponse> updateMyPassword(@CookieValue(value = "token", required = false) String webToken,
                                                         @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                         @Valid @RequestBody UserPwdDto userPwdDto) {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.updatePassword(userEmpCode, userPwdDto);
        return ResponseEntity.ok(new JsonResponse(Constants.SUCCESS));
    }

    // 내 정보 수정(이메일, 전화번호)
    @PutMapping("/users/me")
    public ResponseEntity<JsonResponse> updateMyInfo(@CookieValue(value = "token", required = false) String webToken,
                                                     @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                     @RequestBody UserInfoDto userInfoDto) {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.updateInfo(userEmpCode, userInfoDto);
        return ResponseEntity.ok(new JsonResponse(Constants.SUCCESS));
    }

    // 특정 사용자 정보 조회(헤더 토큰 및 특정 사용자 사원코드 필요)
    @GetMapping("/users/{empCode}")
    public ResponseEntity<UserDto> getUserByEmpCode(@CookieValue(value = "token", required = false) String webToken,
                                                    @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                    @PathVariable String empCode) {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(userEmpCode, Constants.PERMISSION_UPDATE_USER_INFO);
        return ResponseEntity.ok(userService.getUserByEmpCode(empCode));
    }

    // 특정 사용자 권한 변경(헤더 토큰, 특정 사용자 사원코드 및 변경할 roleName)
    @PutMapping("/users/{empCode}")
    public ResponseEntity<JsonResponse> updateRoleByEmpCode(@CookieValue(value = "token", required = false) String webToken,
                                                            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                            @PathVariable String empCode, @RequestParam String roleName) {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(userEmpCode, Constants.PERMISSION_UPDATE_USER_INFO);
        userService.updateRole(empCode, roleName);
        return ResponseEntity.ok(new JsonResponse(Constants.SUCCESS));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDto>> getAllRole(@CookieValue(value = "token", required = false) String webToken,
                                                    @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(userEmpCode, Constants.PERMISSION_UPDATE_USER_INFO);
        return ResponseEntity.ok(userService.getAllRole());
    }

    @GetMapping("/depts")
    public ResponseEntity<List<DeptDto>> getAllDept() {
        return ResponseEntity.ok(userService.getAllDept());
    }
}