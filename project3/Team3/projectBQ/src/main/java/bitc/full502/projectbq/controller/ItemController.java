package bitc.full502.projectbq.controller;

import bitc.full502.projectbq.common.Constants;
import bitc.full502.projectbq.common.response.JsonResponse;
import bitc.full502.projectbq.dto.ItemDto;
import bitc.full502.projectbq.dto.ItemSearchDto;
import bitc.full502.projectbq.dto.ItemStockDto;
import bitc.full502.projectbq.service.ItemService;
import bitc.full502.projectbq.service.MinStockService;
import bitc.full502.projectbq.service.StockService;
import bitc.full502.projectbq.service.UserService;
import bitc.full502.projectbq.util.HeaderUtil;
import bitc.full502.projectbq.util.JwtUtil;
import bitc.full502.projectbq.util.QrUtil;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;
    private final MinStockService minStockService;
    private final StockService stockService;

    //    비품 List (20250917 완료)
    @GetMapping("/item")
    public List<ItemDto> getAllItemList() throws Exception {
        return itemService.getAllItemList();
    }

    @GetMapping("/items/app")
    public List<ItemDto> getAllItemsForApp() {
        return itemService.getAllItemForApp();
    }

    //    앱 전용 "20250917 완료"
    @GetMapping("/items")
    public List<ItemDto> searchItems(ItemSearchDto filter) throws Exception {
        return itemService.searchItemList(filter);
    }

    @GetMapping("/items/web")
    public List<ItemDto> searchItemsForWeb(@CookieValue(value = "token", required = false) String webToken,
                                           ItemSearchDto filter) throws Exception {
        HeaderUtil.checkMissingToken(webToken, null);
        String token = HeaderUtil.getTokenByHeader("Bearer " + webToken);
        String userEmpCode = JwtUtil.getEmpCode(token);
        if (!userService.checkPermission(userEmpCode)) {
            filter.setStatus(Constants.ITEM_STATUS_ACTIVE);
            return itemService.searchItemList(filter);
        }
        return itemService.searchItemList(filter);
    }

    //    비품 상세조회 (비품코드) "20250917 완료"
    @GetMapping("/items/{code}")
    public ItemDto getItemDetail(@PathVariable String code) throws Exception {
        return itemService.getItemDetail(code);
    }

    //    새 비품 등록 "20250917 완료
    @PostMapping("/items")
    public ResponseEntity<ItemDto> createItem(@RequestBody ItemDto itemDto,
                                              @CookieValue(value = "token", required = false) String webToken,
                                              @RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws Exception {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(userEmpCode, Constants.PERMISSION_ADD_ITEM);

        itemDto.setAddUser(userEmpCode);
        ItemDto savedItem = itemService.createItem(itemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    //    비품 정보 수정 20250918 완료
    @PutMapping("/items/{code}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable String code, @RequestBody ItemDto dto) throws Exception {
        ItemDto item = itemService.updateItem(code, dto);
        return ResponseEntity.ok(item);
    }

    // 비품 상태 수정
    @PutMapping("/items/{code}/status")
    public ResponseEntity<ItemDto> updateItemStatus(@CookieValue(value = "token", required = false) String webToken,
                                                    @PathVariable String code, @RequestParam String status) throws Exception {
        HeaderUtil.checkMissingToken(webToken, null);
        String token = HeaderUtil.getTokenByHeader("Bearer " + webToken);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(userEmpCode, Constants.PERMISSION_UPDATE_MIN_STOCK);
        ItemDto item = itemService.updateItemStatus(code, status);
        return ResponseEntity.ok(item);
    }

    //    새비품 등록 승인처리 "20250918 완료"
    @PostMapping("/items/{id}/approve")
    public ResponseEntity<ItemDto> approveItem(@PathVariable Long id,
                                               @CookieValue(value = "token", required = false) String webToken,
                                               @RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws Exception {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(userEmpCode, Constants.PERMISSION_APPROVE_ITEM);
        ItemDto approvedItem = itemService.changeItemStatus(id, userEmpCode, Constants.ITEM_STATUS_ACTIVE);
        minStockService.createDefaultMinStock(approvedItem.getCode());
        stockService.createDefaultStocks(approvedItem.getCode());
        return ResponseEntity.ok(approvedItem);
    }

    //    새비품 등록 반려처리 "20250918 완료"
    @PostMapping("/items/{id}/reject")
    public ResponseEntity<ItemDto> rejectItem(@PathVariable Long id,
                                              @CookieValue(value = "token", required = false) String webToken,
                                              @RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws Exception {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(userEmpCode, Constants.PERMISSION_APPROVE_ITEM);

        ItemDto rejectItem = itemService.changeItemStatus(id, userEmpCode, Constants.ITEM_STATUS_REJECTED);

        return ResponseEntity.ok(rejectItem);
    }

    //    비품 정보 삭제 "20250918 완료"
    @DeleteMapping("/items/{id}")
    public ResponseEntity<JsonResponse> deleteItem(@CookieValue(value = "token", required = false) String webToken,
                                                   @PathVariable String id) throws Exception {
        String token = HeaderUtil.getTokenByHeader("Bearer " + webToken);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(userEmpCode, Constants.PERMISSION_ADD_ITEM);
        itemService.deleteItem(id);
        return ResponseEntity.ok(new JsonResponse(Constants.SUCCESS));
    }

    @GetMapping("/qr/download")
    public ResponseEntity<byte[]> downloadQr(@RequestParam String code) throws WriterException, IOException {
        byte[] qrImage = QrUtil.generateQRImage(code, 300, 300);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qr.png");

        return ResponseEntity.ok().headers(headers).body(qrImage);
    }

    // 신규 비품 삭제
    @PatchMapping("/items/{id}/status")
    public ResponseEntity<ItemDto> deactivateItem(@PathVariable Long id,
                                                  @CookieValue(value = "token", required = false) String webToken,
                                                  @RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws Exception {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(userEmpCode, Constants.PERMISSION_APPROVE_ITEM);

        ItemDto updatedItem = itemService.changeItemStatus(id, userEmpCode, Constants.ITEM_STATUS_INACTIVE);
        return ResponseEntity.ok(updatedItem);
    }

    // 신규 비품 수정
    @PatchMapping("/items/{id}")
    public ResponseEntity<ItemDto> updateItemWithStatus(@PathVariable Long id,
                                                        @RequestBody ItemDto dto,
                                                        @CookieValue(value = "token", required = false) String webToken,
                                                        @RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws Exception {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        userService.checkPermissionsByEmpCode(userEmpCode, Constants.PERMISSION_APPROVE_ITEM);

        ItemDto updatedItem = itemService.updateItemAndStatus(id, dto, Constants.ITEM_STATUS_PENDING);
        return ResponseEntity.ok(updatedItem);
    }


    //    발주 요청 페이지
    @GetMapping("/request/order")
    public List<ItemStockDto> getActiveItem(@CookieValue(value = "token", required = false) String webToken,
                                            @RequestHeader(value = "Authorization", required = false) String authorization,
                                            ItemStockDto dto)
            throws Exception {
        HeaderUtil.checkMissingToken(webToken, authorization);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorization);
        String empCode = JwtUtil.getEmpCode(token);

        userService.checkPermissionsByEmpCode(empCode, Constants.PERMISSION_ADD_ITEM);

        return itemService.getActiveItem(dto);
    }
}
