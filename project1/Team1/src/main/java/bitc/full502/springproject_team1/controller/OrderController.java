package bitc.full502.springproject_team1.controller;

import bitc.full502.springproject_team1.DTO.OrderDTO;
import bitc.full502.springproject_team1.entity.CustomerEntity;
import bitc.full502.springproject_team1.entity.OrderDetailEntity;
import bitc.full502.springproject_team1.entity.OrderEntity;
import bitc.full502.springproject_team1.entity.ProductEntity;
import bitc.full502.springproject_team1.repository.CustomerRepository;
import bitc.full502.springproject_team1.repository.OrderDetailRepository;
import bitc.full502.springproject_team1.repository.OrderRepository;
import bitc.full502.springproject_team1.service.CartService;
import bitc.full502.springproject_team1.service.CustomerService;
import bitc.full502.springproject_team1.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
//

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final ProductService productService;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartService cartService;

    // 로그인 유저 정보 가져오기 전제: 세션 또는 Security 사용
// 단건 결제 진입
    @GetMapping("/submit/{productId}")
    public ModelAndView singleProductOrder(@PathVariable int productId, HttpSession session) throws Exception {
        ModelAndView mv = new ModelAndView("order/order");

//        Integer loginId = (Integer) session.getAttribute("loginId");
//        String loginStrId = (String) session.getAttribute("customerId");

        CustomerEntity customer = (CustomerEntity) session.getAttribute("loginUser");

        if (customer == null) {
            return new ModelAndView("redirect:/login");
        }

        ProductEntity product = productService.selectProductById(productId);


        mv.addObject("productList", List.of(product));
        mv.addObject("customer",    customer);
        mv.addObject("orderDTO",    createOrderDTO(customer));

        return mv;
    }
    //
//     다건 결제 진입
    @GetMapping("/submit")
    public ModelAndView multiProductOrder(@RequestParam List<Integer> productIds,
                                          @RequestParam List<String> orderDetailSize,
                                          @RequestParam List<String> orderDetailColor,
                                          HttpSession session) throws Exception {

        CustomerEntity customer = (CustomerEntity) session.getAttribute("loginUser");

        if (customer == null) {
            return new ModelAndView("redirect:/login");
        }

        List<ProductEntity> list = productIds.stream()
                .map(productService::selectProductById).toList();

        ModelAndView mv = new ModelAndView("order/order");

        mv.addObject("productList",      list);
        mv.addObject("orderDetailSize",  orderDetailSize);
        mv.addObject("orderDetailColor", orderDetailColor);
        mv.addObject("orderDTO",         createOrderDTO(customer));
        mv.addObject("customer",         customer);
        return mv;
    }


    private OrderDTO createOrderDTO(CustomerEntity customer) {
        OrderDTO dto = new OrderDTO();
        dto.setCustomerId(customer.getCustomerIdx());
        dto.setRemainingPoint(customer.getCustomerPoint());
        dto.setUsedCoupon("y".equals(customer.getCustomerCoupon_yn()) ? "y" : "n");
        return dto;
    }

    @PostMapping("/save")
    public String saveOrder(@ModelAttribute OrderDTO orderDTO,
                            @RequestParam("productIds") List<Integer> productIds,
                            @RequestParam("productCounts") List<Integer> productCounts,
                            @RequestParam("orderDetailColor") List<String> colorList,
                            @RequestParam("orderDetailSize") List<String> sizeList,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {  // ✅ 여기에 추가

        CustomerEntity customer = (CustomerEntity) session.getAttribute("loginUser");

        if (customer == null) return "redirect:/login";

        OrderEntity order = new OrderEntity();
        order.setCustomer(customer);
        order.setOrderTotalPrice(orderDTO.getOrderTotalPrice());
        order.setOrderResPrice(orderDTO.getOrderResPrice());
        order.setRemainingPoint(orderDTO.getRemainingPoint());
        order.setOrderDate(LocalDateTime.now());
        order.setProductCount(1);

        // 폼에서 넘어온 쿠폰 사용 여부
        String incomingUsedCoupon = orderDTO.getUsedCoupon();

        // 주문에는 폼 값 그대로 저장
        order.setUsedCoupon(incomingUsedCoupon);

        // 고객 테이블은 쿠폰 사용시에만 N으로 설정
        if (incomingUsedCoupon.equalsIgnoreCase("Y")) {
            customer.setCustomerCoupon_yn("N");
        }

        customer.setCustomerPoint(orderDTO.getRemainingPoint());

        orderRepository.save(order);
        customerRepository.save(customer);

        for (int i = 0; i < productIds.size(); i++) {
            ProductEntity product = productService.selectProductById(productIds.get(i));

            OrderDetailEntity detail = new OrderDetailEntity();

            detail.setOrder(order);
            detail.setProduct(product);
            detail.setOrderDetailPrice(product.getProductPrice());
            detail.setOrderDetailProductCount(productCounts.get(i));

            detail.setOrderDetailColor(colorList.get(i));
            detail.setOrderDetailSize(sizeList.get(i));

            orderDetailRepository.save(detail);
        }

        //  ------------------------- 정환 추가
        cartService.deleteCartItems(customer.getCustomerIdx(), productIds);
        //  ------------------------- 정환 추가

        // ✅ 주문 ID를 넘겨서 단건 주문 목록만 보여주기
        redirectAttributes.addAttribute("orderId", order.getOrderIdx());
        return "redirect:/order/orderlist";
    }


//    @GetMapping("/orderlist")
//    public ModelAndView orderListPage(@RequestParam("orderId") Integer orderId, HttpSession session) {
//        CustomerEntity customer = (CustomerEntity) session.getAttribute("loginCustomer");
//        if (customer == null) return new ModelAndView("redirect:/login");
//
//        // ✅ 단일 주문 조회
//        OrderEntity order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다."));
//
//        List<OrderEntity> orders = List.of(order);  // 단건 주문만 리스트로 전달
//
//        ModelAndView mv = new ModelAndView("order/orderlist");
//        mv.addObject("orders", orders);
//        mv.addObject("customer", customer);
//        return mv;
//    }

    @GetMapping("/orderlist")
    public ModelAndView orderListPage(
            @RequestParam(value = "orderId", required = false) Integer orderId,
            HttpSession session
    ) {
        CustomerEntity customer = (CustomerEntity) session.getAttribute("loginUser");
        if (customer == null) {
            return new ModelAndView("redirect:/login");
        }

        List<OrderEntity> orders;
        if (orderId != null) {
            // 단건 조회
            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 주문이 없습니다: " + orderId));
            orders = List.of(order);
        } else {
            // 전체 조회
            orders = orderRepository.findAllByCustomerOrderByOrderDateDesc(customer);
        }

        ModelAndView mv = new ModelAndView("order/orderlist");
        mv.addObject("orders", orders);
        mv.addObject("customer", customer);
        return mv;
    }

    @GetMapping("/ordersuccess")
    public String orderSuccessPage() {
        return "order/ordersuccess";
    }
}
