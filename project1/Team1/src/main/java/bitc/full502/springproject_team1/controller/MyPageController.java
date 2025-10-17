package bitc.full502.springproject_team1.controller;

import bitc.full502.springproject_team1.entity.CustomerEntity;
import bitc.full502.springproject_team1.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final CustomerService customerService;

    // 🔸 마이페이지 접근 (로그인된 경우만 가능)
    @GetMapping("/mypage")
    public String myPage(Model model, HttpSession session) {
        CustomerEntity loginUser = (CustomerEntity) session.getAttribute("loginUser");
        if (loginUser == null) {
            session.setAttribute("redirectAfterLogin", "/mypage");
            return "redirect:/login";
        }

        System.out.println("===== myPage customer 조회 결과 =====");
        System.out.println("customer ID: " + loginUser.getCustomerId());

        model.addAttribute("customer", loginUser);
        return "my/mypage";
    }

    // 🔸 내 정보 수정 폼 (로그인 필요)
    @GetMapping("/mypage/edit")
    public String editPage(Model model, HttpSession session) {
        Integer loginId = (Integer) session.getAttribute("loginId");
        if (loginId == null) {
            session.setAttribute("redirectAfterLogin", "/mypage/edit"); // 💡 수정 페이지용 리턴 주소 저장
            return "redirect:/login";
        }

        String loginCustomerId = session.getAttribute("customerId").toString();
        CustomerEntity customer = customerService.findByCustomerId(loginCustomerId);
        model.addAttribute("customer", customer);

        return "my/myrevision"; // → templates/my/myrevision.html
    }

    // 🔸 내 정보 수정 저장
    @PostMapping("/mypage/edit")
    public String saveEdit(@ModelAttribute CustomerEntity form,
                           @RequestParam("passwordConfirm") String passwordConfirm,
                           HttpSession session,
                           Model model) {

        String loginCustomerId = (String) session.getAttribute("customerId");
        if (loginCustomerId == null) {
            return "redirect:/login";
        }

        CustomerEntity saved = customerService.findByCustomerId(loginCustomerId);

        // 비밀번호 확인
        if (!saved.getCustomerPass().equals(passwordConfirm)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("customer", saved);
            return "my/myrevision";
        }

        // 정보 수정 (비밀번호 제외)
        saved.setCustomerEmail(form.getCustomerEmail());
        saved.setCustomerAddr(form.getCustomerAddr());
        saved.setCustomerPhone(form.getCustomerPhone());

        customerService.updateCustomer(saved);

        return "redirect:/mypage";
    }
}

