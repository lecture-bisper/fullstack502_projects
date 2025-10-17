package bitc.full502.projectbq.controller;

import bitc.full502.projectbq.common.Constants;
import bitc.full502.projectbq.common.response.JsonResponse;
import bitc.full502.projectbq.dto.MinStockDto;
import bitc.full502.projectbq.service.MinStockService;
import bitc.full502.projectbq.service.StockService;
import bitc.full502.projectbq.service.UserService;
import bitc.full502.projectbq.util.HeaderUtil;
import bitc.full502.projectbq.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MinStockController {

    private final MinStockService minStockService;
    private final StockService stockService;
    private final UserService userService;

    // 전체 적정재고 조회
    @GetMapping("/min-stocks")
    public ResponseEntity<List<MinStockDto>> getAllMinStock(@CookieValue(value = "token", required = false) String webToken,
                                                            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(userEmpCode, Constants.PERMISSION_UPDATE_MIN_STOCK);
        return ResponseEntity.ok(minStockService.getAllMinStock());
    }

    // 아이템 코드로 적정재고 조회 (웹)
    @GetMapping("/min-stocks/item/{itemCode}")
    public ResponseEntity<MinStockDto> getMinStockByItemCode(@PathVariable String itemCode) {
        return ResponseEntity.ok(minStockService.getMinStockByItemCode(itemCode));
    }

    // 정적재고 상태값으로 조회
    @GetMapping("/min-stocks/{status}")
    public ResponseEntity<List<MinStockDto>> getMinStockByStatus(@CookieValue(value = "token", required = false) String webToken,
                                                                 @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                                 @PathVariable String status) {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(userEmpCode, Constants.PERMISSION_UPDATE_MIN_STOCK);
        return ResponseEntity.ok(minStockService.getAllMinStock().stream().
                filter(dto -> status.toUpperCase().equals(dto.getMinStockStatus()))
                .toList());
    }

    // 적정재고의 기준수량(standardQty), 안전수량(safetyQty) 수정
    @PutMapping(value = "/min-stocks/{itemCode}", params = {"standard", "safety"})
    public ResponseEntity<JsonResponse> updateMinStock(@CookieValue(value = "token", required = false) String webToken,
                                                       @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                       @PathVariable String itemCode, @RequestParam long standard, @RequestParam long safety) {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(userEmpCode, Constants.PERMISSION_UPDATE_MIN_STOCK);
        minStockService.updateQuantity(itemCode, standard, safety);
        return ResponseEntity.ok(new JsonResponse(Constants.SUCCESS));
    }

    // 적정재고 상태(status) 수정
    @PutMapping(value = "/min-stocks/{itemCode}", params = "status")
    public ResponseEntity<JsonResponse> updateMinStockStatus(@CookieValue(value = "token", required = false) String webToken,
                                                             @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                             @PathVariable String itemCode, @RequestParam String status) {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(userEmpCode, Constants.PERMISSION_UPDATE_MIN_STOCK);
        minStockService.updateStatus(itemCode, status);
        return ResponseEntity.ok(new JsonResponse(Constants.SUCCESS));
    }
}
