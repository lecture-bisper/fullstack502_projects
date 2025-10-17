package bitc.full502.projectbq.controller;

import bitc.full502.projectbq.common.Constants;
import bitc.full502.projectbq.common.response.JsonResponse;
import bitc.full502.projectbq.dto.*;
import bitc.full502.projectbq.service.StockLogService;
import bitc.full502.projectbq.util.HeaderUtil;
import bitc.full502.projectbq.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StockLogController {

    private final StockLogService stockLogService;

    // 내 출고 로그 조회(앱, LogSearchDto의 keyword, startDate, endDate 사용)
    @PostMapping("/stock-logs/search/me")
    public ResponseEntity<List<StockLogDto>> getMyStockLog(@CookieValue(value = "token", required = false) String webToken,
                                                           @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                           @RequestBody LogSearchDto filter) {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        filter.setEmpCodeOrEmpName(userEmpCode);
        return ResponseEntity.ok(stockLogService.getAllLogByKeyword(filter));
    }

    // 전체 입출고 로그 조회(웹, LogSearchDto 프로퍼티명에 맞게 값 넣어주면 됨 *keyword는 사용안함)
    @PostMapping("/stock-logs/search")
    public ResponseEntity<List<StockLogDto>> getAllStockLog(@RequestBody LogSearchDto filter) {
        return ResponseEntity.ok(stockLogService.getAllLogByFilter(filter));
    }

    // 입출고 로그 등록 테스트용
    @PostMapping("/stock-logs")
    public ResponseEntity<JsonResponse> insertStockLog(@RequestBody StockLogDto stockLogDto) {
        stockLogService.insertStockLog(stockLogDto);
        return ResponseEntity.ok(new JsonResponse(Constants.SUCCESS));
    }

    // 비품별 사용현황
    @GetMapping("/stats/item")
    public ResponseEntity<List<StatisticDto<ItemDto>>> getStatsByItem(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate,
                                                                      @RequestParam String codeOrName) {
        return ResponseEntity.ok(stockLogService.getStatsByItem(codeOrName, startDate, endDate));
    }

    // 사원별 사용현황
    @GetMapping("/stats/user")
    public ResponseEntity<List<StatisticDto<UserDto>>> getStatsByUser(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate,
                                                                      @RequestParam String codeOrName) {
        return ResponseEntity.ok(stockLogService.getStatsByUser(codeOrName, startDate, endDate));
    }
}
