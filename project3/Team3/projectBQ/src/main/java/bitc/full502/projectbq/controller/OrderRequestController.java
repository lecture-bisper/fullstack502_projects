package bitc.full502.projectbq.controller;

import bitc.full502.projectbq.common.Constants;
import bitc.full502.projectbq.domain.entity.item.OrderRequestEntity;
import bitc.full502.projectbq.domain.repository.EmpRepository;
import bitc.full502.projectbq.dto.OrderRequestDto;
import bitc.full502.projectbq.dto.OrderSearchDto;
import bitc.full502.projectbq.service.OrderRequestService;
import bitc.full502.projectbq.service.UserService;
import bitc.full502.projectbq.util.HeaderUtil;
import bitc.full502.projectbq.util.JwtUtil;
import bitc.full502.projectbq.util.OrderRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderRequestController {

    private final OrderRequestService orderRequestService;
    private final UserService userService;
    private final EmpRepository empRepository;

    // 발주 요청 현황 조회
    @GetMapping("/orders")
    public List<OrderRequestDto> getOrderList(@CookieValue(value = "token", required = false) String webToken,
                                              @RequestHeader(value = "Authorization", required = false) String authorization,
                                              OrderSearchDto dto) throws Exception {
        HeaderUtil.checkMissingToken(webToken, authorization);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorization);
        String empCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(empCode, Constants.PERMISSION_ADD_ITEM);

        return orderRequestService.getOrderList(dto);
    }

    // 발주 요청 (토큰 필요)
    @PostMapping("/orders")
    public ResponseEntity<OrderRequestDto> createOrder(
            @CookieValue(value = "token", required = false) String webToken,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody OrderRequestDto dto) throws Exception {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String empCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(empCode, Constants.PERMISSION_ADD_ITEM);

        OrderRequestEntity saved = orderRequestService.createOrder(dto);
        return ResponseEntity.ok(OrderRequestUtil.toDto(saved, empRepository));
    }

    // 발주 승인 (토큰 필요)
    @PostMapping("/orders/{id}/approve")
    public ResponseEntity<OrderRequestDto> approveOrder(
            @CookieValue(value = "token", required = false) String webToken,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id) throws Exception {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String approver = JwtUtil.getEmpCode(token);

        userService.checkPermissionsByEmpCode(approver, Constants.PERMISSION_APPROVE_ITEM);

        OrderRequestEntity approved = orderRequestService.approveOrder(id, approver);
        return ResponseEntity.ok(OrderRequestUtil.toDto(approved, empRepository));
    }

    @PostMapping("/orders/{id}/reject")
    public ResponseEntity<OrderRequestDto> rejectOrder(@CookieValue(value = "token", required = false) String webToken,
                                                       @RequestHeader(value = "Authorization", required = false) String authorization,
                                                       @PathVariable Long id) throws Exception {
        HeaderUtil.checkMissingToken(webToken, authorization);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorization);
        String rejecter = JwtUtil.getEmpCode(token);

        userService.checkPermissionsByEmpCode(rejecter, Constants.PERMISSION_APPROVE_ITEM);

        OrderRequestEntity rejected = orderRequestService.rejectOrder(id, rejecter);

        return ResponseEntity.ok(OrderRequestUtil.toDto(rejected, empRepository));
    }

}
