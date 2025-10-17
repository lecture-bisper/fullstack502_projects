package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.DTO.ProductDTO;
import bitc.full502.springproject_team1.entity.HistoryEntity;
import bitc.full502.springproject_team1.entity.ProductEntity;
import bitc.full502.springproject_team1.repository.HistoryRepository;
import bitc.full502.springproject_team1.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final ProductRepository productRepository;


    @Override
    public void saveHistory(int customerId, int productId) {
        HistoryEntity history = new HistoryEntity();
        history.setCustomerId(customerId);
        history.setProductIdx(productId);
        history.setHistoryDate(LocalDateTime.now());
        historyRepository.save(history);
    }

    @Override
    public List<ProductDTO> getRecentViewedProducts(int customerId) {
        System.out.println("▶▶ getRecentViewedProducts 호출: customerId = " + customerId);

        List<HistoryEntity> histories = historyRepository.findTop5ByCustomerIdOrderByHistoryDateDesc(customerId);
        System.out.println("🟡 조회된 히스토리 수 = " + histories.size());

        List<Integer> productIds = histories.stream()
                .map(HistoryEntity::getProductIdx)
                .distinct()
                .collect(Collectors.toList());

        List<ProductEntity> products = productRepository.findAllById(productIds);

        // historyIdx 포함하여 DTO 생성
        return histories.stream()
                .map(history -> {
                    ProductEntity product = products.stream()
                            .filter(p -> Integer.valueOf(p.getProductId()).equals(history.getProductIdx()))  // Integer equals 사용
                            .findFirst()
                            .orElse(null);
                    if (product != null) {
                        return new ProductDTO(product, history.getHistoryIdx());
                    }
                    return null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteHistoryById(Integer historyIdx) {
        historyRepository.deleteById(historyIdx);
    }
}
