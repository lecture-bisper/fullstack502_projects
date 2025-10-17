package bitc.full502.projectbq.service;

import bitc.full502.projectbq.common.Constants;
import bitc.full502.projectbq.domain.entity.item.CategoryEntity;
import bitc.full502.projectbq.domain.entity.item.ItemEntity;
import bitc.full502.projectbq.domain.entity.item.MinStockEntity;
import bitc.full502.projectbq.domain.entity.item.StockEntity;
import bitc.full502.projectbq.domain.repository.*;
import bitc.full502.projectbq.dto.ItemDto;
import bitc.full502.projectbq.dto.ItemSearchDto;
import bitc.full502.projectbq.dto.ItemStockDto;
import bitc.full502.projectbq.util.ItemUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final EmpRepository empRepository;
    private final StockRepository stockRepository;
    private final MinStockRepository minStockRepository;

    //    비품 List 20250917 완료
    @Override
    public List<ItemDto> getAllItemList() throws Exception {
        List<ItemDto> itemList = ItemUtil.convertToItemDTOList(itemRepository.findAllByOrderByAddDateDesc(), empRepository);
        itemList.forEach(item -> {
            List<StockEntity> stockList = stockRepository.findAllByItemId(item.getId());
            item.setAllQuantity(stockList.stream().mapToLong(StockEntity::getQuantity).sum());
        });
        return itemList;
    }

    //    비품 검색 & 필터 (20250817 완료)
    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItemList(ItemSearchDto filter) throws Exception {
        // 앱 통합 검색
        if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
            List<ItemEntity> items = itemRepository.searchItemsAll(filter.getKeyword().trim());
            return ItemUtil.convertToItemDTOList(items, empRepository);
        }

        // 웹 검색 필터링
        String name = (filter.getName() == null || filter.getName().isBlank()) ? null : filter.getName().trim();
        String manufacturer = (filter.getManufacturer() == null || filter.getManufacturer().isBlank()) ? null : filter.getManufacturer().trim();
        String code = (filter.getCode() == null || filter.getCode().isBlank()) ? null : filter.getCode().trim();
        String status = (filter.getStatus() == null || filter.getStatus().isBlank()) ? null : filter.getStatus().trim();
        List<ItemEntity> items = itemRepository.searchItems(
                name,
                manufacturer,
                code,
                filter.getCategoryId(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                filter.getStartDate(),
                filter.getEndDate(),
                status
        );
        List<ItemDto> itemList = ItemUtil.convertToItemDTOList(items, empRepository);
        itemList.forEach(item -> {
            List<StockEntity> stockList = stockRepository.findAllByItemId(item.getId());
            item.setAllQuantity(stockList.stream().mapToLong(StockEntity::getQuantity).sum());
        });
        return itemList;
    }


    //    비품 상세조회 (비품코드) "20250917 완료"
    @Override
    public ItemDto getItemDetail(String code) throws Exception {
        ItemEntity entity = itemRepository.findByCode(code);
        if (entity == null) {
            throw new IllegalArgumentException("해당 비품이 존재하지 않습니다." + code);
        }
        return ItemUtil.convertToItemDTO(entity, empRepository);
    }

    //    새 비품 등록 "20250918 완료"
    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto) throws Exception {

        CategoryEntity category = categoryRepository.findById(itemDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 카테고리 ID"));

        ItemEntity itemEntity = ItemUtil.convertToItemEntity(itemDto, category);

//        재고/적정재고 초기화
//        StockEntity stock = new StockEntity();
//        stock.setItem(itemEntity);
//        stock.setQuantity(0);
//        itemEntity.setStock(stock);/**/
//
//        MinStockEntity minStock = new MinStockEntity();
//        minStock.setItem(itemEntity);
//        minStock.setSafetyQty(0);
//        minStock.setStandardQty(0);
//        itemEntity.setMinStock(minStock);

        ItemEntity savedItem = itemRepository.save(itemEntity);

        return ItemUtil.convertToItemDTO(savedItem, empRepository);
    }

    //  비품 정보 수정 "20250918 완료"
    @Override
    public ItemDto updateItem(String code, ItemDto dto) throws Exception {
        ItemEntity entity = itemRepository.findByCode(code);

        if (entity == null) {
            throw new IllegalArgumentException("해당 비품이 존재하지 않습니다. (code=" + code + ")");
        }

        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 카테고리 ID"));

        entity.setCategory(category);
        entity.setName(dto.getName());
        entity.setManufacturer(dto.getManufacturer());
        entity.setPrice(dto.getPrice());
        entity.setApproveUser(dto.getApproveUser());
        entity.setStatus(dto.getStatus());

        ItemEntity updatedEntity = itemRepository.save(entity);

        return ItemUtil.convertToItemDTO(updatedEntity, empRepository);
    }

    //    비품 정보 삭제 "20250918 완료"
    @Override
    @Transactional
    public void deleteItem(String id) throws Exception {
        ItemEntity entity = itemRepository.findById(Long.parseLong(id))
                .orElseThrow(()-> new IllegalArgumentException("Item ID is missing or invalid."));
        itemRepository.delete(entity);
    }

    //    새 비품 등록 상태 처리 "20250918 완료"
    @Override
    @Transactional
    public ItemDto changeItemStatus(Long id, String userEmpCode, String status) throws Exception {
        ItemEntity item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item Not Found"));


        item.setApproveUser(userEmpCode);
        item.setAddDate(LocalDateTime.now());
        item.setStatus(status);

//        승인시 자동으로 코드 생성 "20250919 완료"
        if (Constants.ITEM_STATUS_ACTIVE.equals(status) && (item.getCode() == null || item.getCode().isBlank())) {
            item.setCode(ItemUtil.generateItemCode(item));
        }

        ItemEntity saved = itemRepository.save(item);

        return ItemUtil.convertToItemDTO(saved, empRepository);
    }

    // 상태와 데이터를 동시에 수정
    @Override
    @Transactional
    public ItemDto updateItemAndStatus(Long id, ItemDto dto, String status) throws Exception {
        // 1. Item 조회
        ItemEntity item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 비품이 존재하지 않습니다. (id=" + id + ")"));

        // 2. dto 값 업데이트
        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getManufacturer() != null) item.setManufacturer(dto.getManufacturer());
        if (dto.getPrice() != null) item.setPrice(dto.getPrice());

        if (dto.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 카테고리 ID"));
            item.setCategory(category);
        }

        // 3. 상태 변경
        if (status != null) {
            item.setStatus(status);
            item.setApproveUser(dto.getApproveUser()); // 필요 시 결재자 정보도 갱신
            item.setAddDate(LocalDateTime.now());

            // 승인 상태라면 코드 생성
            if (Constants.ITEM_STATUS_ACTIVE.equals(status) && (item.getCode() == null || item.getCode().isBlank())) {
                item.setCode(ItemUtil.generateItemCode(item));
            }
        }

        // 4. 저장
        ItemEntity saved = itemRepository.save(item);

        // 5. DTO 변환
        return ItemUtil.convertToItemDTO(saved, empRepository);
    }

    @Override
    public ItemDto updateItemStatus(String code, String status) {
        ItemEntity entity = itemRepository.findByCode(code);
        if (entity == null) {
            throw new IllegalArgumentException("해당 비품이 존재하지 않습니다. (code=" + code + ")");
        }
        entity.setStatus(status);
        return ItemUtil.convertToItemDTO(itemRepository.save(entity), empRepository);
    }

    @Override
    public List<ItemDto> getAllItemForApp() {
        return ItemUtil.convertToItemDTOList(itemRepository.findAllByOrderByAddDateDesc());
    }

    @Override
    public List<ItemStockDto> getActiveItem(ItemStockDto dto) throws Exception {
        List<ItemEntity> items = itemRepository.findByStatus(
                Constants.ITEM_STATUS_ACTIVE,
                dto.getKeyword(),
                dto.getManufacturer(),
                dto.getCategoryId(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getMinStockStatus()
        );


        return items.stream()
                .map(item -> {
                    List<StockEntity> stocks = stockRepository.findAllByItemId(item.getId());
                    MinStockEntity minStock = minStockRepository.findByItem(item);

                    return ItemUtil.toStockDto(item, stocks, minStock);
                }).collect(Collectors.toList());
    }
}
