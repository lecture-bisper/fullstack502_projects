package bitc.full502.projectbq.service;

import bitc.full502.projectbq.common.Constants;
import bitc.full502.projectbq.domain.entity.item.ItemEntity;
import bitc.full502.projectbq.domain.entity.item.MinStockEntity;
import bitc.full502.projectbq.domain.entity.item.OrderRequestEntity;
import bitc.full502.projectbq.domain.entity.item.StockEntity;
import bitc.full502.projectbq.domain.repository.*;
import bitc.full502.projectbq.dto.OrderRequestDto;
import bitc.full502.projectbq.dto.OrderSearchDto;
import bitc.full502.projectbq.util.OrderRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderRequestServiceImpl implements OrderRequestService {

    private final OrderRequestRepository orderRequestRepository;
    private final ItemRepository itemRepository;
    private final MinStockRepository minStockRepository;
    private final EmpRepository empRepository;
    private final StockRepository stockRepository;


    // 발주 요청
    @Override
    public OrderRequestEntity createOrder(OrderRequestDto dto) throws Exception {
        ItemEntity item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new RuntimeException("비품을 찾을 수 없습니다."));

        OrderRequestEntity order = OrderRequestEntity.builder()
                .item(item)
                .requestQty(dto.getRequestQty())
                .requestUser(dto.getRequestUser())
                .comment(dto.getComment())
                .status(Constants.REQUEST_STATUS_REQUESTED)
                .build();

        MinStockEntity minStock = minStockRepository.findByItem(item);
        if (minStock != null) {
            minStock.setStatus(Constants.MIN_STOCK_STATUS_PENDING);
            minStockRepository.save(minStock);
        }

        return orderRequestRepository.save(order);
    }

    // 발주 승인
    public OrderRequestEntity approveOrder(Long id, String approverEmpCode) throws Exception {
        OrderRequestEntity order = orderRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("발주 요청을 찾을 수 없습니다."));

        order.setStatus(Constants.REQUEST_STATUS_APPROVED);
        order.setApproveUser(approverEmpCode);
        orderRequestRepository.save(order);

//        MinStockEntity minStock = minStockRepository.findByItem(order.getItem());
//        if (minStock != null && Constants.MIN_STOCK_S TATUS_PENDING.equals(minStock.getStatus())) {
//            minStock.setStatus(Constants.MIN_STOCK_STATUS_OK);
//            minStockRepository.save(minStock);
//        }

        return orderRequestRepository.save(order);
    }

//    비품 발주 현황 조회
    @Override
    public List<OrderRequestDto> getOrderList(OrderSearchDto dto) throws Exception {
        List<OrderRequestEntity> orders = orderRequestRepository.findByFilters(
                dto.getCategoryId(),
                dto.getStatus(),
                dto.getKeyword(),
                dto.getManufacturer(),
                dto.getStartDate(),
                dto.getEndDate()
        );

        return orders.stream().map(order -> {
            OrderRequestDto response = OrderRequestUtil.toDto(order,empRepository);

            MinStockEntity minStock = minStockRepository.findByItem(order.getItem());
            if (minStock != null) {
                response.setStandardQty(minStock.getStandardQty());
                response.setSafetyQty(minStock.getSafetyQty());
            }

            List<StockEntity> stocks = stockRepository.findAllByItemId(order.getItem().getId());
            long stockQuantity = stocks.stream()
                    .mapToLong(StockEntity::getQuantity)
                    .sum();

            response.setStockQuantity(stockQuantity);

            return response;
        }).toList();

    }
// 반려
    @Override
    public OrderRequestEntity rejectOrder(Long id, String rejecter) throws Exception {
        OrderRequestEntity order =orderRequestRepository.findById(id)
                .orElseThrow(() -> new Exception("발주 요청을 찾을 수 없습니다."));

        order.setStatus(Constants.REQUEST_STATUS_REJECTED);
        order.setApproveUser(rejecter);
        return orderRequestRepository.save(order);
    }
}
