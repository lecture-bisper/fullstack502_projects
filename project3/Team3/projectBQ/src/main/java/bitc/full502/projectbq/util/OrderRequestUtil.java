package bitc.full502.projectbq.util;

import bitc.full502.projectbq.domain.entity.item.ItemEntity;
import bitc.full502.projectbq.domain.entity.item.MinStockEntity;
import bitc.full502.projectbq.domain.entity.item.OrderRequestEntity;
import bitc.full502.projectbq.domain.entity.user.EmpEntity;
import bitc.full502.projectbq.domain.repository.EmpRepository;
import bitc.full502.projectbq.dto.OrderRequestDto;

public class OrderRequestUtil {

    public static OrderRequestDto toDto (OrderRequestEntity entity, EmpRepository empRepo) {
        ItemEntity item = entity.getItem();

        String userCode = entity.getRequestUser();
        String userName = empRepo.findByCode(userCode).map(EmpEntity::getName).orElse(userCode);

        String approverCode = entity.getApproveUser();
        String approverName = (approverCode !=null && !approverCode.isEmpty())
                ? empRepo.findByCode(approverCode).map(EmpEntity::getName).orElse(approverCode) : null;

        return  OrderRequestDto.builder()
                .id(entity.getId())
                .itemId(item.getId())
                .name(item.getName())
                .code(item.getCode())
                .manufacturer(item.getManufacturer())
                .category(item.getCategory().getName())
                .categoryKrName(item.getCategory().getKrName())
                .requestQty(entity.getRequestQty())
                .price(item.getPrice())
                .requestUser(userCode)
                .requestUserName(userName)
                .status(entity.getStatus())
                .requestDate(entity.getRequestDate())
                .comment(entity.getComment())
                .approveUser(approverCode)
                .approveUserName(approverName)
                .build();

    }
}
