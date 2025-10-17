package bitc.full502.projectbq.service;

import bitc.full502.projectbq.domain.repository.WarehouseRepository;
import bitc.full502.projectbq.dto.WarehouseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;

    @Override
    public List<WarehouseDto> getAllWarehouse() throws Exception {
        return warehouseRepository.findAll().stream()
                .map(w -> new WarehouseDto(w.getId(), w.getName(), w.getKrName()))
                .toList();
    }
}
