package bitc.full502.springproject_team1.controller;

import bitc.full502.springproject_team1.entity.CustomerEntity;
import bitc.full502.springproject_team1.DTO.ProductDTO;
import bitc.full502.springproject_team1.service.HistoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/mypage/recentview")
    public String recentViewPage(HttpSession session, Model model) {
        CustomerEntity loginUser = (CustomerEntity) session.getAttribute("loginUser");
        if (loginUser != null) {
            List<ProductDTO> recentProducts = historyService.getRecentViewedProducts(loginUser.getCustomerIdx());
            model.addAttribute("recentProducts", recentProducts);
        }
        return "my/recentview";
    }

    // 삭제 요청 (Ajax)
    @DeleteMapping("/mypage/history/delete")
    @ResponseBody
    public String deleteHistory(@RequestParam("historyIdx") Integer historyIdx, HttpSession session) {
        CustomerEntity loginUser = (CustomerEntity) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "로그인이 필요합니다.";
        }
        historyService.deleteHistoryById(historyIdx);
        return "삭제 완료";
    }
}
