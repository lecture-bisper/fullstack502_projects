package bitc.full502.springproject_team1.controller;

import bitc.full502.springproject_team1.service.WishService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    // ✅ 찜 목록 보기 (마이페이지)
    @GetMapping("/mypage/wishlist")
    public String wishlistPage(HttpSession session, Model model) {
        Integer loginId = (Integer) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/login";

        List<?> wishlist = wishService.findByCustomerId(loginId);
        model.addAttribute("wishlist", wishlist);
        return "my/wishlist";
    }

    // ✅ 찜 추가 (AJAX)
    @PostMapping("/wish/add")
    @ResponseBody
    public ResponseEntity<String> addWish(@RequestBody Map<String, Object> body, HttpSession session) {
        Integer loginId = (Integer) session.getAttribute("loginId");
        if (loginId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        int productId = (int) body.get("productId");

        if (wishService.isWished(loginId, productId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 찜한 상품입니다");
        }

        wishService.addWish(loginId, productId);
        return ResponseEntity.ok("찜 완료");
    }

    // ✅ 찜 삭제 (폼 제출 방식)
    @PostMapping("/wishlist/remove")
    public ResponseEntity<Void> removeWishPost(@RequestParam int productId, HttpSession session) {
        Integer loginId = (Integer) session.getAttribute("loginId");
        if (loginId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  // 내용 없이 상태코드만
        }

        try {
            wishService.removeWish(loginId, productId);
            return ResponseEntity.ok().build();  // 성공했지만 메시지 없음
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 찜 삭제 (AJAX)
    @PostMapping("/wishlist/remove-ajax")
    @ResponseBody
    public ResponseEntity<?> removeWish(@RequestParam("productId") int productId, HttpSession session) {
        Integer loginId = (Integer) session.getAttribute("loginId");
        if (loginId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        try {
            wishService.removeWish(loginId, productId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
        }
    }
}
