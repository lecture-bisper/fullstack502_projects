package bitc.full502.projectbq.service;

import bitc.full502.projectbq.common.Constants;
import bitc.full502.projectbq.domain.entity.item.ItemEntity;
import bitc.full502.projectbq.domain.entity.item.StockEntity;
import bitc.full502.projectbq.domain.entity.item.StockLogEntity;
import bitc.full502.projectbq.domain.entity.item.WarehouseEntity;
import bitc.full502.projectbq.domain.repository.ItemRepository;
import bitc.full502.projectbq.domain.repository.StockLogRepository;
import bitc.full502.projectbq.domain.repository.StockRepository;
import bitc.full502.projectbq.domain.repository.WarehouseRepository;
import bitc.full502.projectbq.dto.StockDto;
import bitc.full502.projectbq.dto.StockRequestDto;
import bitc.full502.projectbq.dto.StockSearchDto;
import bitc.full502.projectbq.util.StockUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final StockLogRepository stockLogRepository;
//    private final @Lazy MinStockService minStockService;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;

    //    창고 목록 조회 "20250918 완료"
    @Override
    public List<StockDto> getAllStockList() throws Exception {
        return StockUtil.convertToStockDTOList(stockRepository.findAll());
    }

    //    창고별 비품 조회
    @Override
    public List<StockDto> searchStocks(StockSearchDto filter) throws Exception {
        List<StockEntity> stocks = stockRepository.searchStocks(
                filter.getName(),
                filter.getManufacturer(),
                filter.getCategory(),
                filter.getWarehouseId()
        );
        return StockUtil.convertToStockDTOList(stocks);
    }

    //    비품별 재고 조회 "20250922 완료"
    @Override
    public List<StockDto> getStockByItem(String code) throws Exception {
        List<StockEntity> stocks = stockRepository.findByCode(code);
        return StockUtil.convertToStockDTOList(stocks);
    }

    @Override
    public List<StockDto> getStocksByItemCode(String code) {
        List<StockEntity> stocks = stockRepository.findByCode(code);
        if (!stocks.get(0).getItem().getStatus().equals(Constants.ITEM_STATUS_ACTIVE))
            throw new IllegalArgumentException("Item is not active");
        return StockUtil.convertToStockDTOList(stocks);
    }

    //    입고 등록 "20250922 완료"
    @Override
    public StockDto increaseStock(StockRequestDto request, MinStockService minStockService) throws Exception {
        StockEntity stocks = stockRepository.findByCodeAndWarehouseId(
                request.getCode(), request.getWarehouseId()
        ).orElseThrow(() -> new RuntimeException("재고 정보를 찾을 수 없습니다."));

        stocks.setQuantity(stocks.getQuantity() + request.getQuantity());
        StockEntity update = stockRepository.save(stocks);

        StockLogEntity log = StockLogEntity.builder()
                .itemId(stocks.getItem().getId())
                .warehouse(stocks.getWarehouse())
                .empCode(request.getEmpCode())
                .type(Constants.STOCK_LOG_TYPE_IN)
                .quantity(request.getQuantity())
                .memo(request.getRemark())
                .build();
        stockLogRepository.save(log);

        // 안전재고 체크
        minStockService.checkSafetyQuantity(stocks.getItem(),
                getAllQuantityByItemId(stocks.getItem().getId()));
        return StockUtil.convertToDTO(update);
    }

    //    출고 등록 "20250922 완료"
    @Override
    public StockDto decreaseStock(StockRequestDto request, MinStockService minStockService) throws Exception {
        StockEntity stocks = stockRepository.findByCodeAndWarehouseId(
                request.getCode(), request.getWarehouseId()
        ).orElseThrow(() -> new RuntimeException("재고 정보를 찾을 수 없습니다."));

        if (stocks.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("재고 수량 부족 : 출고 불가");
        }

        stocks.setQuantity(stocks.getQuantity() - request.getQuantity());
        StockEntity updated = stockRepository.save(stocks);

        StockLogEntity log = StockLogEntity.builder()
                .itemId(stocks.getItem().getId())
                .warehouse(stocks.getWarehouse())
                .empCode(request.getEmpCode())
                .type(Constants.STOCK_LOG_TYPE_OUT)
                .quantity(request.getQuantity())
                .memo(request.getRemark())
                .build();
        stockLogRepository.save(log);

        // 안전재고 체크
        minStockService.checkSafetyQuantity(stocks.getItem(),
                getAllQuantityByItemId(stocks.getItem().getId()));
        return StockUtil.convertToDTO(updated);
    }

    @Override
    public long getAllQuantityByItemId(long id) {
        List<StockEntity> stock = stockRepository.findAllByItemId(id);
        return stock.stream().mapToLong(StockEntity::getQuantity).sum();
    }

    @Override
    public void createDefaultStocks(String itemCode) {
        ItemEntity itemEntity = itemRepository.findByCode(itemCode);
        if (itemEntity == null) throw new IllegalArgumentException("Item not found");
        List<WarehouseEntity> whEntityList = warehouseRepository.findAll();
        whEntityList.forEach(warehouse -> {
            StockEntity stockEntity = StockEntity.builder()
                    .item(itemEntity)
                    .warehouse(warehouse)
                    .build();
            stockRepository.save(stockEntity);
        });
    }
}
