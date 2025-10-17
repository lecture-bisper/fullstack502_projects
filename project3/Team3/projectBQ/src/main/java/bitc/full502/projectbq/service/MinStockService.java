package bitc.full502.projectbq.service;

import bitc.full502.projectbq.domain.entity.item.ItemEntity;
import bitc.full502.projectbq.dto.MinStockDto;

import java.util.List;

public interface MinStockService {

    List<MinStockDto> getAllMinStock();

    MinStockDto getMinStockByItemCode(String itemCode);

    void updateQuantity(String itemCode, long standardQty, long safetyQty);

    void updateStatus(String itemCode, String status);

    void createDefaultMinStock(String itemCode);

    void checkSafetyQuantity(ItemEntity item, long allQuantity);
}
