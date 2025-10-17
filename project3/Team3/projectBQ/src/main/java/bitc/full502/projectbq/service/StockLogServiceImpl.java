package bitc.full502.projectbq.service;

import bitc.full502.projectbq.common.Constants;
import bitc.full502.projectbq.domain.entity.item.ItemEntity;
import bitc.full502.projectbq.domain.entity.item.StockLogEntity;
import bitc.full502.projectbq.domain.entity.item.WarehouseEntity;
import bitc.full502.projectbq.domain.entity.user.EmpEntity;
import bitc.full502.projectbq.domain.repository.*;
import bitc.full502.projectbq.dto.*;
import bitc.full502.projectbq.util.Util;
import bitc.full502.projectbq.util.ItemUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockLogServiceImpl implements StockLogService {

    private final StockLogRepository stockLogRepository;
    private final ItemRepository itemRepository;
    private final EmpRepository empRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    @Override
    public List<StockLogDto> getAllLogByFilter(LogSearchDto filter) {
        LocalDateTime start = filter.getStartDate() != null ? filter.getStartDate().atStartOfDay() : null;
        LocalDateTime end = filter.getEndDate() != null ? filter.getEndDate().atTime(23, 59, 59) : null;

        List<StockLogEntity> entityList = stockLogRepository.findAllByFilter(filter, start, end);
        List<StockLogDto> dtoList = new ArrayList<>();

        entityList.forEach(stockLogEntity -> {
            ItemEntity item = itemRepository.findById(stockLogEntity.getItemId()).orElseThrow(
                    () -> new IllegalArgumentException("Item ID is missing or invalid."));
            EmpEntity emp = empRepository.findByCode(stockLogEntity.getEmpCode()).orElseThrow(
                    () -> new IllegalArgumentException("Employee is missing or invalid."));
            dtoList.add(Util.toStockLogDto(stockLogEntity, item, emp));
        });
        return dtoList;
    }

    @Override
    public List<StockLogDto> getAllLogByKeyword(LogSearchDto filter) {
        LocalDateTime start = filter.getStartDate() != null ? filter.getStartDate().atStartOfDay() : null;
        LocalDateTime end = filter.getEndDate() != null ? filter.getEndDate().atTime(23, 59, 59) : null;

        List<StockLogEntity> entityList = stockLogRepository.findAllByKeyword(
                filter.getEmpCodeOrEmpName(), Constants.STOCK_LOG_TYPE_OUT, filter.getKeyword(), start, end);

        List<StockLogDto> dtoList = new ArrayList<>();

        entityList.forEach(stockLogEntity -> {
            ItemEntity item = itemRepository.findById(stockLogEntity.getItemId()).orElseThrow(
                    () -> new IllegalArgumentException("Item ID is missing or invalid."));
            EmpEntity emp = empRepository.findByCode(stockLogEntity.getEmpCode()).orElseThrow(
                    () -> new IllegalArgumentException("Employee is missing or invalid."));
            dtoList.add(Util.toStockLogDto(stockLogEntity, item, emp));
        });
        return dtoList;
    }


    @Override
    public void insertStockLog(StockLogDto stockLogDto) {
        WarehouseEntity warehouse = warehouseRepository.findById(stockLogDto.getWarehouseId()).orElseThrow(
                () -> new IllegalArgumentException("Warehouse ID is missing or invalid."));
        StockLogEntity log = StockLogEntity.builder()
                .itemId(stockLogDto.getItemId())
                .warehouse(warehouse)
                .empCode(stockLogDto.getEmpCode())
                .type(stockLogDto.getType())
                .quantity(stockLogDto.getQuantity())
                .memo(stockLogDto.getMemo())
                .build();
        stockLogRepository.save(log);
    }

    @Override
    public List<StatisticDto<ItemDto>> getStatsByItem(String keyword, LocalDate startDate, LocalDate endDate) {
        List<StatisticDto<ItemDto>> statisticList = stockLogRepository.findDistinctByItemId(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        statisticList.forEach(statistic -> {
            statistic.setInfo(ItemUtil.convertToItemDTO(itemRepository.findById(statistic.getItemId()).orElseThrow(
                    () -> new IllegalArgumentException("Item ID is missing or invalid.")), empRepository));
            statistic.setTotalPrice(statistic.getTotalQuantity() * statistic.getInfo().getPrice());
        });

        return Util.filterItemStats(statisticList, keyword);
    }

    @Override
    public List<StatisticDto<UserDto>> getStatsByUser(String codeOrName, LocalDate startDate, LocalDate endDate) {
        // 1. 아이템 단위로 묶어서 조회
        List<StatisticDto<UserDto>> rawStats = stockLogRepository.findDistinctByEmpCode(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        // 2. 사원별 합산 및 총 금액 계산
        Map<String, StatisticDto<UserDto>> totalByEmp = new HashMap<>();

        for (StatisticDto<UserDto> dto : rawStats) {
            String empCode = dto.getInfoId();
            long itemId = dto.getItemId();

            // 아이템 가격 조회
            ItemEntity item = itemRepository.findById(itemId).orElseThrow(
                    () -> new IllegalArgumentException("Item ID is missing or invalid.")
            );
            long totalPriceForItem = dto.getTotalQuantity() * item.getPrice();

            // 사원 정보 조회 (한 번만)
            EmpEntity emp = empRepository.findByCode(empCode).orElseThrow(
                    () -> new IllegalArgumentException("Employee is missing or invalid.")
            );
            UserDto userDto = Util.toUserDto(userRepository.findByEmp(emp).orElseThrow(
                    () -> new IllegalArgumentException("User is missing or invalid.")
            ));

            // 사원별 합산
            totalByEmp.compute(empCode, (k, v) -> {
                if (v == null) {
                    return StatisticDto.<UserDto>builder()
                            .info(userDto)
                            .infoId(empCode)
                            .totalQuantity(dto.getTotalQuantity())
                            .totalPrice(totalPriceForItem)
                            .latestDate(dto.getLatestDate())
                            .build();
                } else {
                    v.setTotalQuantity(v.getTotalQuantity() + dto.getTotalQuantity());
                    v.setTotalPrice(v.getTotalPrice() + totalPriceForItem);
                    if (dto.getLatestDate().isAfter(v.getLatestDate())) {
                        v.setLatestDate(dto.getLatestDate());
                    }
                    return v;
                }
            });
        }

        // 3. Map → List
        List<StatisticDto<UserDto>> result = new ArrayList<>(totalByEmp.values());

        // 4. 코드/이름 필터링
        return Util.filterUserStats(result, codeOrName);
    }
}
