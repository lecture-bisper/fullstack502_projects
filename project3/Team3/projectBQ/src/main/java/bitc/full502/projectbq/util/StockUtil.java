package bitc.full502.projectbq.util;

import bitc.full502.projectbq.domain.entity.item.StockEntity;
import bitc.full502.projectbq.dto.StockDto;

import java.util.List;
import java.util.stream.Collectors;

public class StockUtil {

    // StockEntity -> StockDto 변환
    public static StockDto convertToDTO(StockEntity stock) {

        return new StockDto(
                stock.getId(),
                stock.getItem().getName(),
                stock.getItem().getCode(),
                stock.getItem().getManufacturer(),
                stock.getQuantity(),
                stock.getItem().getCategory().getKrName(),
                stock.getWarehouse().getName(),
                stock.getWarehouse().getKrName()
        );
    }

    // List<StockEntity> -> List<StockDto>
    public static List<StockDto> convertToStockDTOList(List<StockEntity> stockEntities) {
        return stockEntities.stream()
                .map(StockUtil :: convertToDTO)
                .collect(Collectors.toList());
    }
}
