package bitc.full502.projectbq.service;

import bitc.full502.projectbq.dto.ItemDto;
import bitc.full502.projectbq.dto.ItemSearchDto;
import bitc.full502.projectbq.dto.ItemStockDto;

import java.util.List;

public interface ItemService {

//    비품 List (20250917 완료)
    List<ItemDto> getAllItemList() throws Exception;

//    비품 검색 & 필터 (20250917 완료)
    List<ItemDto> searchItemList(ItemSearchDto items) throws Exception;

//    비품 상세조회 (비품코드) "20250917 완료"
    ItemDto getItemDetail(String code) throws Exception;

//     새 비품 등록 "20250917 완료"
    ItemDto createItem(ItemDto itemDto) throws Exception;

//    비품 정보 수정 "20250918 완료"
    ItemDto updateItem(String code, ItemDto dto) throws Exception;

//    비품 정보 삭제 "20250918 완료"
    void deleteItem(String code) throws Exception;

//    새 비품 등록 상태처리 "20250918 완료"
    ItemDto changeItemStatus(Long id, String userEmpCode, String status) throws Exception;

//    새 비품 정보 & 상태 수정
    ItemDto updateItemAndStatus(Long id, ItemDto dto, String status) throws Exception;

//    발주 요청
    List<ItemStockDto> getActiveItem(ItemStockDto dto) throws Exception;

    ItemDto updateItemStatus(String code, String status);

    List<ItemDto> getAllItemForApp();
}
