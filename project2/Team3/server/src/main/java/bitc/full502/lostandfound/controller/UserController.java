package bitc.full502.lostandfound.controller;

import bitc.full502.lostandfound.dto.UserDTO;
import bitc.full502.lostandfound.service.JpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final JpaService jpaService;

    // 로그인 시 토큰으로 유저 정보 불러오기 (UserDTO 타입으로 변환하여 반환)
    @GetMapping("/info")
    public UserDTO getUserInfo(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws Exception {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new UserDTO();
        }

        String token = authorizationHeader.substring(7);
        return jpaService.getUserInfo(jpaService.getUserIdByToken(token));
    }
}
