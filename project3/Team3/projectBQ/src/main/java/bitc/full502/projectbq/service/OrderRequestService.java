package bitc.full502.projectbq.service;

import bitc.full502.projectbq.domain.entity.item.OrderRequestEntity;
import bitc.full502.projectbq.dto.OrderRequestDto;
import bitc.full502.projectbq.dto.OrderSearchDto;

import java.util.List;

public interface OrderRequestService {

//    발주 요청
    OrderRequestEntity createOrder(OrderRequestDto dto) throws Exception;
//    발주 승인
    OrderRequestEntity approveOrder(Long id, String approver) throws Exception;

    List<OrderRequestDto> getOrderList(OrderSearchDto dto) throws Exception;

    OrderRequestEntity rejectOrder(Long id, String rejecter) throws Exception;
}
