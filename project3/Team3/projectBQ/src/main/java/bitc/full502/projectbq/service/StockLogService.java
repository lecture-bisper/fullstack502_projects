package bitc.full502.projectbq.service;

import bitc.full502.projectbq.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface StockLogService {

    List<StockLogDto> getAllLogByFilter(LogSearchDto filter);

    List<StockLogDto> getAllLogByKeyword(LogSearchDto filter);

    void insertStockLog(StockLogDto stockLogDto);

    List<StatisticDto<ItemDto>> getStatsByItem(String keyword, LocalDate startDate, LocalDate endDate);

    List<StatisticDto<UserDto>> getStatsByUser(String codeOrName, LocalDate startDate, LocalDate endDate);
}
