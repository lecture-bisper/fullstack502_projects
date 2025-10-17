package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.DTO.ProductDTO;

import java.util.List;

public interface HistoryService {
    List<ProductDTO> getRecentViewedProducts(int customerIdx);

    void deleteHistoryById(Integer historyIdx);

    void saveHistory(int customerId, int productId);
}
