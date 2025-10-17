
package bitc.full502.springproject_team1.controller;

import bitc.full502.springproject_team1.DTO.CartDTO;
import bitc.full502.springproject_team1.entity.CartEntity;
import bitc.full502.springproject_team1.service.CartService;
import bitc.full502.springproject_team1.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    @GetMapping("/order/cart")
    public String cartPage(HttpSession session, Model model) {
        Integer loginId = (Integer) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/login";

        List<CartEntity> cartList = cartService.findByCustomerId(loginId);
        model.addAttribute("cartList", cartList);
        return "order/cart"; // cart.html 경로
    }


    @PostMapping("/cart/add")
    @ResponseBody
    public String addCart(@RequestBody CartDTO cartDTO, HttpSession session) {
        Integer loginId = (Integer) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/login";

        CartDTO newCart =  new CartDTO();
        newCart.setCustomerId(loginId);
        newCart.setProductId(cartDTO.getProductId());
        newCart.setCartColor(String.valueOf(cartDTO.getCartColor()));
        newCart.setCartSize(String.valueOf(cartDTO.getCartSize()));
        productService.saveCart(newCart);
        return "저장 완료";
    }

    @PostMapping("/cart/delete")
    @ResponseBody
    public String deleteCart(@RequestParam int cartIdx) {
        cartService.deleteByCartId(cartIdx);
        return "삭제 완료";
    }

    @PostMapping("/cart/deleteAll")
    @ResponseBody
    public String deleteAll(HttpSession session) {
        Integer loginId = (Integer) session.getAttribute("loginId");
        if (loginId == null) return "로그인 필요";

        cartService.deleteByCustomerId(loginId);
        return "전체 삭제 완료";
    }
}
