package bitc.full502.projectbq.service;

import bitc.full502.projectbq.dto.WarehouseDto;

import java.util.List;

public interface WarehouseService {

    List<WarehouseDto> getAllWarehouse() throws Exception;
}
