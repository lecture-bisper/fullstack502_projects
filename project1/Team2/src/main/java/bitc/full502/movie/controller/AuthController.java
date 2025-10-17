package bitc.full502.movie.controller;

import bitc.full502.movie.domain.entity.UserEntity;
import bitc.full502.movie.service.AuthService;
import bitc.full502.movie.service.JPAService;
import bitc.full502.movie.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JPAService jpaService;

    private final AuthService authService;
    private final UserService userService;

    // 로그인 페이지로 이동
    @GetMapping("/login")
    public String LoginPage() throws Exception {
        return "auth/login";
    }

    // 로그인 프로세스 (ID, PW, Servlet Request 를 매개변수로 받음)
    @PostMapping("/loginProcess")
    @ResponseBody
    public String loginProcess(@RequestParam("userId") String userId,
                               @RequestParam("userPw") String userPw,
                               HttpServletRequest req) throws Exception {

        // 로그인 성공 여부 확인
        boolean loginSuccess = authService.login(userId, userPw);

        if (loginSuccess) {
            // 로그인 성공 시 사용자 정보 조회
            UserEntity user = authService.selectUserInfo(userId);

            // 세션 가져오기
            HttpSession session = req.getSession();

            // 세션에 사용자 정보 저장 (필드명은 UserEntity에 맞게 조정)
            session.setAttribute("loginUser", user);
            session.setAttribute("userId", user.getId());

            // 세션 유지 시간 설정(초 단위)
            session.setMaxInactiveInterval(60 * 10);

            // 로그인 성공 페이지로 리다이렉트
            return "success";

        } else {
            // 로그인 실패 시 로그인 실패 페이지로 리다이렉트
            return "fail";
        }
    }

    // 로그아웃 프로세스
    @RequestMapping("/logoutProcess")
    public String logout(HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/main";
    }


    // 회원가입 페이지로 이동
    @GetMapping("/join")
    public String JoinPage() throws Exception {
        return "auth/join";
    }

    // 아이디 중복 체크
    @GetMapping("/duplicateCheck")
    @ResponseBody
    public Boolean duplicateCheck(@RequestParam("userId") String userId) throws Exception {
        return jpaService.duplicateCheck(userId);
    }

    // 회원가입 프로세스 (정보를 UserEntity로 받음)
    @PostMapping("/joinProcess")
    @ResponseBody
    public String JoinProcess(@RequestBody UserEntity user) throws Exception {
        UserEntity savedUser = jpaService.registerUser(user);

        if (savedUser != null) return "success";
        else return "fail";
    }

    //    아이디 찾기 페이지 이동
    @GetMapping("/lostid")
    public String lostid(HttpServletRequest req) throws Exception {

        return "auth/lostid";
    }

    //    아이디 찾기 프로세스
    @PostMapping("/findId")
    @ResponseBody
    public ResponseEntity<String> findUserId(@RequestParam String userName,
                                             @RequestParam String email) throws Exception {
        Optional<UserEntity> userOpt = authService.findByNameAndEmail(userName, email);
        return userOpt.map(userEntity -> ResponseEntity.ok(userEntity.getId())).orElseGet(() ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 회원이 없습니다."));
    }


    //    비밀번호 찾기 페이지 이동
    @GetMapping("/lostpw")
    public String lostpw(HttpServletRequest req) throws Exception {
        return "auth/lostpw";
    }

    //    비밀번호 찾기 프로세스
    @PostMapping("/findPw")
    @ResponseBody
    public ResponseEntity<String> findUserPw(@RequestParam String userId,
                                             @RequestParam String email) throws Exception {
        Optional<UserEntity> userOpt = authService.findByIdAndEmail(userId, email);
        return userOpt.map(userEntity ->
                        ResponseEntity.ok("비밀번호는: " + userEntity.getPassword())) // 또는 reset 페이지로 유도
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 회원이 없습니다."));
    }

    @DeleteMapping("/deleteUser")
    @ResponseBody
    public String deleteUser(HttpSession session) throws Exception {
        userService.deleteUser((String) session.getAttribute("userId"));
        return "success";
    }
}
