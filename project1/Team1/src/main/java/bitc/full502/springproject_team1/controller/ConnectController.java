package bitc.full502.springproject_team1.controller;

import bitc.full502.springproject_team1.entity.CustomerEntity;
import bitc.full502.springproject_team1.repository.CustomerRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class ConnectController {

    private final CustomerRepository customerService;

    //    ========================cart controller 임시=================================
//    @GetMapping("/order/cart")
//    public String myPage(HttpSession session, Model model) {
//        // 세션에서 로그인 정보 확인
//        String userIdStr = String.valueOf(session.getAttribute("customerId"));
//
//        if (userIdStr == null) {
//            // 로그인 안 되어 있으면 로그인 페이지로 리다이렉트
//            return "redirect:/login";
//        }
//
//        // 로그인된 유저 정보 불러오기
//        CustomerEntity customer = customerService.findByCustomerId("customerId");
//        model.addAttribute("customer", customer);
//
//        return "/order/cart"; // → templates/mypage.html
//    }
}
