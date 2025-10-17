//package bitc.full502.springproject_team1.controller;
//
//import bitc.full502.springproject_team1.entity.OrderEntity;
//import bitc.full502.springproject_team1.entity.ProductEntity;
//import bitc.full502.springproject_team1.entity.ReviewEntity;
//import bitc.full502.springproject_team1.repository.ProductRepository;
//import bitc.full502.springproject_team1.service.ReviewService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.util.List;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/review")
//public class ReviewController {
//
//    private final ReviewService reviewService;
//    private final ProductRepository productRepository;
//
//    // 리뷰 상세보기
//    @GetMapping("/detail/{reviewIdx}")
//    public String detail(@PathVariable Integer reviewIdx, Model model) {
//        ReviewEntity review = reviewService.findById(reviewIdx);
//        model.addAttribute("review", review);
//        return "review/detail";
//    }
//
//    // 리뷰 리스트
//    @GetMapping("/list")
//    public String list(Model model) {
//        List<ReviewEntity> reviews = reviewService.findAll();
//        model.addAttribute("reviews", reviews);
//        return "review/list";
//    }
//
//    // 리뷰 작성 폼 (신규)
//    @GetMapping("/new")
//    public String createForm(@RequestParam(value="orderIdx", required=false) Integer orderIdx, Model model) {
//        if (orderIdx == null) {
//            return "redirect:/order/orderlist";
//        }
//
//        OrderEntity order = reviewService.findOrderById(orderIdx);
//        if (order == null) {
//            return "redirect:/order/orderlist";
//        }
//
//        ReviewEntity review = new ReviewEntity();
//        review.setOrder(order);
//
//        model.addAttribute("review", review);  // ✅ 반드시 있어야 함
//        model.addAttribute("order", order);
//        return "review/form";
//    }
//
//    // 리뷰 수정 폼
//    @GetMapping("/update/{reviewIdx}")
//    public String updateForm(@PathVariable Integer reviewIdx, Model model) {
//        ReviewEntity review = reviewService.findById(reviewIdx);
//        model.addAttribute("review", review);
//
//        // ★★★ 수정 시에도 order 객체 넘기기
//        model.addAttribute("order", review.getOrder());
//        System.out.println("리뷰 수정폼: order 정보 = " + review.getOrder());
//
//        return "review/form";
//    }
//
//    // 리뷰 저장 (신규 및 수정)
//    @PostMapping("/save")
//    public String save(@ModelAttribute ReviewEntity review,
//                       @RequestParam("orderIdx") Integer orderIdx,
//                       Model model) {
//
//        System.out.println("리뷰 저장 시도");
//        System.out.println("orderIdx: " + orderIdx);
//        System.out.println("review: " + review);
//
//        OrderEntity order = reviewService.findOrderById(orderIdx);
//        System.out.println("조회된 order: " + order);
//        if (order == null) {
//            System.out.println("주문 정보 없음!");
//            model.addAttribute("message", "주문 정보 없음");
//            model.addAttribute("review", review);
//            return "review/form";
//        }
//
//        ProductEntity product = productRepository.findById(order.getProductId()).orElse(null);
//        System.out.println("조회된 product: " + product);
//        if (product == null) {
//            System.out.println("상품 정보 없음!");
//            model.addAttribute("message", "상품 정보 없음");
//            model.addAttribute("review", review);
//            model.addAttribute("order", order);
//            return "review/form";
//        }
//
//        if (review.getReviewIdx() == null && reviewService.existsByOrderIdx(orderIdx)) {
//            System.out.println("이미 리뷰 존재함!");
//            model.addAttribute("message", "이미 리뷰가 있음");
//            model.addAttribute("review", reviewService.findByOrderIdx(orderIdx));
//            model.addAttribute("order", order);
//            return "review/form";
//        }
//
//        review.setOrder(order);
//        review.setProduct(product);
//
//        reviewService.save(review);
//        System.out.println("리뷰 저장 완료!");
//
//        return "redirect:/order/orderlist";
//    }
//
//    // 리뷰 삭제
//    @GetMapping("/delete/{reviewIdx}")
//    public String delete(@PathVariable Integer reviewIdx, RedirectAttributes rttr) {
//        reviewService.deleteById(reviewIdx);
//        rttr.addFlashAttribute("message", "리뷰가 삭제되었습니다!");
//        return "redirect:/mypage/mywrite";
//    }
//}

package bitc.full502.springproject_team1.controller;

import bitc.full502.springproject_team1.entity.OrderEntity;
import bitc.full502.springproject_team1.entity.OrderDetailEntity;
import bitc.full502.springproject_team1.entity.ProductEntity;
import bitc.full502.springproject_team1.repository.OrderRepository;
import bitc.full502.springproject_team1.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @GetMapping("/form/{orderId}")
    public String reviewForm(@PathVariable Integer orderId, Model model) {
        // 1) 주문 조회
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id: " + orderId));

//        // 2) OrderDetailEntity 리스트에서 productId 꺼내서 ProductEntity 조회
//        List<ProductEntity> products = order.getOrderDetailList().stream()
//                .map(detail -> productRepository
//                        .findById(detail.getProductId())
//                        .orElse(null)
//                )
//                .collect(Collectors.toList());

        List<ProductEntity> products = order.getOrderDetailList().stream()
                .map(OrderDetailEntity::getProduct)
                .collect(Collectors.toList());

        // 3) 뷰에 전달
        model.addAttribute("order", order);
        model.addAttribute("products", products);
        return "review/form";  // 실제 뷰 경로에 맞게 조정하세요
    }

    // TODO: POST 리뷰 저장 메서드 등 추가 구현
}
