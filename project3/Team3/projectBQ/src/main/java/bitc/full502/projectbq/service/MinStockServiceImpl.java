package bitc.full502.projectbq.service;

import bitc.full502.projectbq.common.Constants;
import bitc.full502.projectbq.domain.entity.item.ItemEntity;
import bitc.full502.projectbq.domain.entity.item.MinStockEntity;
import bitc.full502.projectbq.domain.entity.item.StockEntity;
import bitc.full502.projectbq.domain.repository.ItemRepository;
import bitc.full502.projectbq.domain.repository.MinStockRepository;
import bitc.full502.projectbq.dto.MinStockDto;
import bitc.full502.projectbq.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinStockServiceImpl implements MinStockService {

    private final MinStockRepository minStockRepository;
    private final StockService stockService;
    private final ItemRepository itemRepository;

    @Override
    public List<MinStockDto> getAllMinStock() {
        List<MinStockEntity> minList = minStockRepository.findAll();
        List<MinStockDto> dtoList = new ArrayList<>();

        for (MinStockEntity minStockEntity : minList) {
            long allQuantity = stockService.getAllQuantityByItemId(minStockEntity.getItem().getId());
            MinStockDto dto = Util.toMinStockDto(minStockEntity);
            dto.setStockQuantity(allQuantity);
            dto.setStandardQty(minStockEntity.getStandardQty());
            dto.setSafetyQty(minStockEntity.getSafetyQty());
            dto.setMinStockStatus(minStockEntity.getStatus());
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public MinStockDto getMinStockByItemCode(String itemCode) {
        ItemEntity item = itemRepository.findByCode(itemCode);
        long allQuantity = stockService.getAllQuantityByItemId(item.getId());

        if (minStockRepository.findByItem(item) == null) {
            stockService.createDefaultStocks(itemCode);
            createDefaultMinStock(itemCode);
        }

        MinStockEntity minStockEntity = minStockRepository.findByItem(item);
        MinStockDto dto = Util.toMinStockDto(minStockEntity);
        dto.setStockQuantity(allQuantity);
        dto.setStandardQty(minStockEntity.getStandardQty());
        dto.setSafetyQty(minStockEntity.getSafetyQty());
        dto.setMinStockStatus(minStockEntity.getStatus());
        return dto;
    }

    @Override
    public void updateQuantity(String itemCode, long standardQty, long safetyQty) {
        ItemEntity item = itemRepository.findByCode(itemCode);
        MinStockEntity minStock = minStockRepository.findByItem(item);
        minStock.setStandardQty(standardQty);
        minStock.setSafetyQty(safetyQty);
        minStockRepository.save(minStock);
    }

    @Override
    public void updateStatus(String itemCode, String status) {
        ItemEntity item = itemRepository.findByCode(itemCode);
        MinStockEntity minStock = minStockRepository.findByItem(item);
        minStock.setStatus(status);
        minStockRepository.save(minStock);
    }

    // 새 비품 추가 시 적정재고 추가 메소드
    @Override
    public void createDefaultMinStock(String itemCode) {
        ItemEntity itemEntity = itemRepository.findByCode(itemCode);
        if (itemEntity == null) throw new IllegalArgumentException("Item not found");
        MinStockEntity minStockEntity = new MinStockEntity();
        minStockEntity.setItem(itemEntity);
        minStockRepository.save(minStockEntity);
    }

    @Override
    public void checkSafetyQuantity(ItemEntity item, long allQuantity) {
        if (!item.getStatus().equals(Constants.ITEM_STATUS_ACTIVE))
            throw new IllegalArgumentException("Item is not active");

        MinStockEntity minStock = minStockRepository.findByItem(item);
        // 전체수량이 안전재고 미만이고
        if (minStock.getSafetyQty() > allQuantity) {
            // 적정재고 상태가 OK 일 때 LOW로 변경
            if (minStock.getStatus().equals(Constants.MIN_STOCK_STATUS_OK)) {
                minStock.setStatus(Constants.MIN_STOCK_STATUS_LOW);
                minStockRepository.save(minStock);
            }
            // 전체수량이 안전재고 이상이고
        } else {
            // 적정재고 상태가 LOW 또는 PENDING 일 때 OK로 변경
            if (!minStock.getStatus().equals(Constants.MIN_STOCK_STATUS_OK)) {
                minStock.setStatus(Constants.MIN_STOCK_STATUS_OK);
                minStockRepository.save(minStock);
            }
        }
    }
}
