package bitc.full502.projectbq.util;

import bitc.full502.projectbq.common.Constants;
import bitc.full502.projectbq.domain.entity.item.CategoryEntity;
import bitc.full502.projectbq.domain.entity.item.ItemEntity;
import bitc.full502.projectbq.domain.entity.item.MinStockEntity;
import bitc.full502.projectbq.domain.entity.item.StockEntity;
import bitc.full502.projectbq.domain.entity.user.EmpEntity;
import bitc.full502.projectbq.domain.repository.EmpRepository;
import bitc.full502.projectbq.dto.ItemDto;
import bitc.full502.projectbq.dto.ItemStockDto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ItemUtil {

    // ItemEntity -> ItemDto 변환 "20250917 완료"
    public static ItemDto convertToItemDTO(ItemEntity itemEntity, EmpRepository empRepository) {
        String addUserName = "";
        String approvedUserName = "";

        if (itemEntity.getAddUser() != null && !itemEntity.getAddUser().isBlank()) {
            addUserName = empRepository.findByCode(itemEntity.getAddUser())
                    .map(EmpEntity::getName)
                    .orElse("");
        }

        if (itemEntity.getApproveUser() != null && !itemEntity.getApproveUser().isBlank()) {
            approvedUserName = empRepository.findByCode(itemEntity.getApproveUser())
                    .map(EmpEntity::getName)
                    .orElse("");
        }

        return new ItemDto(
                itemEntity.getId(),
                itemEntity.getCategory().getId(),
                itemEntity.getCategory().getKrName(),
                itemEntity.getCode(),
                itemEntity.getName(),
                itemEntity.getManufacturer(),
                itemEntity.getPrice(),
                0L,
                itemEntity.getAddDate(),
                itemEntity.getAddUser(),
                itemEntity.getApproveUser(),
                addUserName,
                approvedUserName,
                itemEntity.getStatus()
        );
    }

    // List<ItemEntity> -> List<ItemDto> "20250917 완료"
    public static List<ItemDto> convertToItemDTOList(List<ItemEntity> itemEntities, EmpRepository empRepository) {
        return itemEntities.stream()
                .map(entity -> convertToItemDTO(entity, empRepository))
                .collect(Collectors.toList());
    }

    //    ItemDto -> ItemEntity 변환 "20250917 완료"
    public static ItemEntity convertToItemEntity(ItemDto dto, CategoryEntity category) {
        return ItemEntity.builder()
                .category(category)
                .code(dto.getCode())
                .name(dto.getName())
                .manufacturer(dto.getManufacturer())
                .price(dto.getPrice())
                .addUser(dto.getAddUser())
                .build();
    }

    //    새 비품 생성 시 자동으로 코드 생성 "20250919 완료"
    public static String generateItemCode(ItemEntity item) {
        if (item.getId() == 0 || item.getCategory().getId() == 0 || item.getAddDate() == null) {
            throw new IllegalArgumentException("비품코드 생성에 필요한 데이터가 부족합니다.");
        }

        String itemNo = String.format("%03d", item.getId() % 1000);
        String categoryNo = String.format("%02d", item.getCategory().getId() % 1000);
        String date = item.getAddDate().format(DateTimeFormatter.ofPattern("yyMMdd"));

        return itemNo + categoryNo + date;

    }

    //    발주 요청
    public static ItemStockDto toStockDto(ItemEntity item, List<StockEntity> stocks, MinStockEntity minStock) {
        long stockQuantity = stocks.stream()
                .mapToLong(StockEntity::getQuantity)
                .sum();

        String minStockStatus = Constants.MIN_STOCK_STATUS_OK;

        if (minStock != null) {
            if (minStock.getStatus() != null) {
                minStockStatus = minStock.getStatus();
            } else if (stockQuantity < minStock.getStandardQty()) {
                minStockStatus = Constants.MIN_STOCK_STATUS_LOW;
            } else {
                minStockStatus = Constants.MIN_STOCK_STATUS_OK;
            }
        }
        return ItemStockDto.builder()
                .id(item.getId())
                .code(item.getCode())
                .name(item.getName())
                .manufacturer(item.getManufacturer())
                .price(item.getPrice())
                .categoryId(item.getCategory().getId())
                .categoryName(item.getCategory().getName())
                .categoryKrName(item.getCategory().getKrName())
                .stockQuantity(stockQuantity)
                .standardQty(minStock != null ? minStock.getStandardQty() : 0L)
                .safetyQty(minStock != null ? minStock.getSafetyQty() : 0L)
                .minStockStatus(minStockStatus)
                .build();
    }

    public static ItemDto convertToItemDTO(ItemEntity itemEntity) {
        CategoryEntity category = itemEntity.getCategory();
        return ItemDto.builder()
                .id(itemEntity.getId())
                .categoryId(category.getId())
                .categoryName(category.getKrName())
                .code(itemEntity.getCode())
                .name(itemEntity.getName())
                .manufacturer(itemEntity.getManufacturer())
                .price(itemEntity.getPrice())
                .status(itemEntity.getStatus())
                .build();
    }

    public static List<ItemDto> convertToItemDTOList(List<ItemEntity> itemEntity) {
        return itemEntity.stream()
                .map(ItemUtil::convertToItemDTO)
                .collect(Collectors.toList());
    }
}
