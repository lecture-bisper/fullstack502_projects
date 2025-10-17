package bitc.full502.springproject_team1.controller;

import bitc.full502.springproject_team1.entity.*;
import bitc.full502.springproject_team1.repository.*;
import bitc.full502.springproject_team1.service.CustomerService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private Integer getLoginId(HttpSession session)  {
        Object loginObj = session.getAttribute("loginId");
        if (loginObj instanceof Integer) {
            return (Integer) loginObj;
        } else if (loginObj instanceof String) {
            try {
                return Integer.parseInt((String) loginObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // 내가 쓴 글 (게시글, 댓글, 리뷰)
    @GetMapping("/mypage/mywrite")
    public String myWritePage(Model model, HttpSession session) {
        Integer loginId = (Integer) session.getAttribute("loginId");
        System.out.println("로그인 ID: " + loginId);

        if (loginId == null) return "redirect:/login";

        CustomerEntity customer = customerService.findByIdx(loginId);
        System.out.println("조회된 고객: " + customer);

        // 게시글 목록
        List<BoardEntity> boards = boardRepository.findByCustomer(customer);

        // 댓글 목록
        List<BoardCommentEntity> comments = boardCommentRepository.findByCustomer(customer);

        // 리뷰: 주문 → 리뷰
        List<OrderEntity> orders = orderRepository.findByCustomer_CustomerIdx(loginId);
        List<Integer> orderIdxList = orders.stream().map(OrderEntity::getOrderIdx).toList();
        List<ReviewEntity> reviews = reviewRepository.findByOrder_OrderIdxIn(orderIdxList);

        model.addAttribute("customer", customer);
        model.addAttribute("boards", boards);
        model.addAttribute("comments", comments);
        model.addAttribute("reviews", reviews);

        return "my/mywrite"; // templates/my/mywrite.html

    }

    @GetMapping("/boardDetail/{boardIdx}")
    public String boardDetail(@PathVariable Integer boardIdx, Model model) {
        Optional<BoardEntity> boardOpt = boardRepository.findById(boardIdx);
        if (boardOpt.isEmpty()) {
            // 게시글이 없으면 메인 페이지 등으로 리다이렉트
            return "redirect:/";
        }

        model.addAttribute("board", boardOpt.get());
        return "board/boardDetail"; // templates/board/detail.html
    }

//    @GetMapping("/order/orderlist")
//    public String orderList(HttpSession session, Model model) {
//        Integer loginId = (Integer) session.getAttribute("loginId");
//        if (loginId == null) return "redirect:/login";
//
//        CustomerEntity customer = customerService.findByIdx(loginId);
//        List<OrderEntity> orders = orderRepository.findByCustomer_CustomerIdx(loginId);
//
//        // 로그 출력: 주문 수
//        System.out.println("주문 내역 수: " + orders.size());
//
//        // 🟡 productMap 구성 (상품 정보)
//        Map<Integer, ProductEntity> productMap = new HashMap<>();
//        for (OrderEntity order : orders) {
//            Integer pid = order.getProductId();
//            productRepository.findById(pid).ifPresent(product -> {
//                productMap.put(order.getOrderIdx(), product);
//            });
//        }
//
//        // ✅ 리뷰 작성된 주문번호 리스트 뽑기
//        List<Integer> orderIdxList = orders.stream().map(OrderEntity::getOrderIdx).toList();
//        List<ReviewEntity> reviews = reviewRepository.findByOrder_OrderIdxIn(orderIdxList);
//        List<Integer> reviewOrderIdxList = reviews.stream()
//                .map(r -> r.getOrder().getOrderIdx())
//                .toList();
//
//        // 로그 출력: 리뷰 작성된 주문 번호 리스트
//        System.out.println("리뷰 작성된 주문 번호 리스트: " + reviewOrderIdxList);
//
//        // 👉 모델에 추가
//        model.addAttribute("loginId", loginId);
//        model.addAttribute("customer", customer);
//        model.addAttribute("orders", orders);
//        model.addAttribute("productMap", productMap);
//        model.addAttribute("reviewOrderIdxList", reviewOrderIdxList); // ✅ 꼭 추가
//        return "order/orderlist";
//    }

//    @GetMapping("/customer/orderlist")
//    public String orderList(HttpSession session, Model model) {
//        Integer loginId = (Integer) session.getAttribute("loginId");
//        if (loginId == null) return "redirect:/login";
//
//        CustomerEntity customer = customerService.findByIdx(loginId);
//        List<OrderEntity> orders = orderRepository.findByCustomer_CustomerIdx(loginId);
//
//        // 로그 출력: 주문 수
//        System.out.println("주문 내역 수: " + orders.size());
//
//        // 🟡 productMap 구성 (상품 정보)
//        Map<Integer, ProductEntity> productMap = new HashMap<>();
//        for (OrderEntity order : orders) {
//            Integer pid = order.getProductId();
//            productRepository.findById(pid).ifPresent(product -> {
//                productMap.put(order.getOrderIdx(), product);
//            });
//        }
//
//        // ✅ 리뷰 작성된 주문번호 리스트 뽑기
//        List<Integer> orderIdxList = orders.stream().map(OrderEntity::getOrderIdx).toList();
//        List<ReviewEntity> reviews = reviewRepository.findByOrder_OrderIdxIn(orderIdxList);
//        List<Integer> reviewOrderIdxList = reviews.stream()
//                .map(r -> r.getOrder().getOrderIdx())
//                .toList();
//
//        // 로그 출력: 리뷰 작성된 주문 번호 리스트
//        System.out.println("리뷰 작성된 주문 번호 리스트: " + reviewOrderIdxList);
//
//        // 👉 모델에 추가
//        model.addAttribute("loginId", loginId);
//        model.addAttribute("customer", customer);
//        model.addAttribute("orders", orders);
//        model.addAttribute("productMap", productMap);
//        model.addAttribute("reviewOrderIdxList", reviewOrderIdxList); // ✅ 꼭 추가
//        return "order/orderlist";
//    }



}
