
package bitc.full502.springproject_team1.controller;

import bitc.full502.springproject_team1.DTO.CustomerDTO;
import bitc.full502.springproject_team1.entity.CustomerEntity;
import bitc.full502.springproject_team1.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class AuthController {

    private final AuthService authService;

    // 메인 페이지
    @GetMapping("")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    // 회원가입 폼
    @GetMapping("/join")
    public ModelAndView join() {
        return new ModelAndView("/join/join");
    }

    // 회원가입 처리
    @RequestMapping("/joinProcess")
    public String JoinProcess(@ModelAttribute CustomerDTO cdto) throws Exception {
        authService.joinUser(cdto);
        return "redirect:/";
    }

    // 로그인 폼
    @GetMapping("/login")
    public String login() {
        return "/join/login";
    }

    // 로그인 처리
    @PostMapping("/loginProcess")
    public String loginProcess(@RequestParam("customerId") String customerId,
                               @RequestParam("customerPass") String customerPass,
                               HttpServletRequest req) throws Exception {

        int result = authService.isUserInfo(customerId, customerPass);

        if (result == 1) {
            CustomerEntity customer = authService.selectUserInfo(customerId);
            HttpSession session = req.getSession();

            if (session != null) session.invalidate();
            session = req.getSession(true);

            // ✅ 세션에 로그인 사용자 저장
            session.setAttribute("loginUser", customer);
            session.setAttribute("loginCustomer", customer);
            session.setAttribute("loginId", customer.getCustomerIdx()); // 고객 PK
            session.setAttribute("customerId", customerId);
            session.setAttribute("customerIdx", customer.getCustomerIdx());
            session.setMaxInactiveInterval(60 * 60); // 60분 유지

            // 로그인 전 리디렉션 URL 있으면 이동
            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            if (redirectUrl != null) {
                session.removeAttribute("redirectAfterLogin");
                return "redirect:" + redirectUrl;
            }

            return "redirect:/";
        } else {
            // 로그인 실패
            return "/join/login";
        }
    }

    // 비밀번호 찾기 폼
    @GetMapping("/password")
    public ModelAndView findPassword() {
        return new ModelAndView("/join/password");
    }

    // 비밀번호 찾기 처리
    @PostMapping("/findPassword")
    @ResponseBody
    public Map<String, Object> findPassword(@RequestParam String customerId, @RequestParam String customerEmail) {
        Map<String, Object> result = new HashMap<>();
        Optional<CustomerEntity> customerOpt = authService.findPassword(customerId, customerEmail);
        if (customerOpt.isPresent()) {
            CustomerEntity customer = customerOpt.get();
            result.put("success", true);
            result.put("name", customer.getCustomerName());
            result.put("password", customer.getCustomerPass());
        } else {
            result.put("success", false);
        }
        return result;
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 초기화
        return "redirect:/";
    }

    // 중복 로그인
    @GetMapping("/idCheck")
    @ResponseBody
    public Map<String, Boolean> idCheck(@RequestParam String customerId) {
        boolean isDuplicate = authService.checkDuplicateId(customerId);

        Map<String, Boolean> result = new HashMap<>();
        result.put("duplicate", isDuplicate);
        return result;
    }

}
