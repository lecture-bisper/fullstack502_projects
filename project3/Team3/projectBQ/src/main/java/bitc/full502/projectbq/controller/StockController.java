package bitc.full502.projectbq.controller;

import bitc.full502.projectbq.dto.StockDto;
import bitc.full502.projectbq.dto.StockRequestDto;
import bitc.full502.projectbq.dto.StockSearchDto;
import bitc.full502.projectbq.dto.WarehouseDto;
import bitc.full502.projectbq.service.MinStockService;
import bitc.full502.projectbq.service.StockService;
import bitc.full502.projectbq.service.WarehouseService;
import bitc.full502.projectbq.util.HeaderUtil;
import bitc.full502.projectbq.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final WarehouseService warehouseService;
    private final MinStockService minStockService;

    //    창고 목록 조회 "20250918 완료"
    @GetMapping("/stocks")
    public List<StockDto> getAllStock() throws Exception {
        return stockService.getAllStockList();
    }

    //    창고별 비품 조회 "20250922 완료"
    @GetMapping("/stock/search")
    public List<StockDto> searchStock(StockSearchDto filter) throws Exception {
        return stockService.searchStocks(filter);
    }

    //    비품별 재고 조화 "20250922 완료"
    @GetMapping("/stocks/{code}")
    public List<StockDto> stockByItem(@PathVariable String code) throws Exception {
        return stockService.getStockByItem(code);
    }

    @GetMapping("/stocks/app/{code}")
    public List<StockDto> stockByItemForApp(@PathVariable String code) throws Exception {
        return stockService.getStocksByItemCode(code);
    }

    //    입고 등록 "20250922 완료"
    @PostMapping("/stock/in")
    private StockDto stockIn(@CookieValue(value = "token", required = false) String webToken,
                             @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                             @RequestBody StockRequestDto request) throws Exception {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        request.setEmpCode(userEmpCode);
        return stockService.increaseStock(request, minStockService);
    }

    //    출고 등록 "20250922 완료"
    @PostMapping("/stock/out")
    public StockDto stockOut(@CookieValue(value = "token", required = false) String webToken,
                             @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                             @RequestBody StockRequestDto request) throws Exception {
        HeaderUtil.checkMissingToken(webToken, authorizationHeader);
        String token = HeaderUtil.getTokenByHeader((webToken != null) ? "Bearer " + webToken : authorizationHeader);
        String userEmpCode = JwtUtil.getEmpCode(token);
        request.setEmpCode(userEmpCode);
        return stockService.decreaseStock(request, minStockService);
    }

    //    창고 조회 "20250922 완료"
    @GetMapping("/warehouse")
    public List<WarehouseDto> getAllWarehouse() throws Exception {
        return warehouseService.getAllWarehouse();
    }

}
