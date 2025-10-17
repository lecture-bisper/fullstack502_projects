package bitc.full502.springproject_team1.controller;//package bitc.full502.mypage.controller;
//
//import bitc.full502.mypage.domain.entity.CustomerEntity;
//import bitc.full502.mypage.service.CustomerService;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//@Controller
//@RequiredArgsConstructor
//public class FirstPageController {
//
//    private final CustomerService customerService;
//
//    @GetMapping("/")
//    public String firstPage(Model model, HttpSession session) {
//        Integer loginId = (Integer) session.getAttribute("loginId");
//
//        if (loginId != null) {
//            CustomerEntity customer = customerService.findById(loginId);
//            model.addAttribute("customer", customer);
//        }
//
//        return "first"; // first.html
//    }
//
//    @GetMapping("/mypage")
//    public String myPage(Model model, HttpSession session) {
//        Integer loginId = (Integer) session.getAttribute("loginId");
//
//        if (loginId == null) {
//            // 개발용 강제 로그인 처리 (PK=1번 사용자가 있다고 가정)
//            loginId = 1;
//            session.setAttribute("loginId", loginId);
//        }
//
//        CustomerEntity customer = customerService.findById(loginId);
//        model.addAttribute("customer", customer);
//
//        return "mypage"; // mypage.html
//    }
//}
