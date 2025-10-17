package bitc.full502.projectbq.service;

import bitc.full502.projectbq.dto.StockDto;
import bitc.full502.projectbq.dto.StockRequestDto;
import bitc.full502.projectbq.dto.StockSearchDto;

import java.util.List;

public interface StockService {

    //    창고 목록 조회 "20250918 완료"
    List<StockDto> getAllStockList() throws Exception;

    //    창고별 비품 조회 "20250922완료"
    List<StockDto> searchStocks(StockSearchDto filter) throws Exception;

    //  비품별 재고 조회 "20250922 완료"
    List<StockDto> getStockByItem(String code) throws Exception;

    List<StockDto> getStocksByItemCode(String code);

    //    입고 등록 "20250922 완료"
    StockDto increaseStock(StockRequestDto request, MinStockService minStockService) throws Exception;

    //    출고 등록 "20250922 완료"
    StockDto decreaseStock(StockRequestDto request, MinStockService minStockService) throws Exception;

    long getAllQuantityByItemId(long id);

    void createDefaultStocks(String itemCode);
}
