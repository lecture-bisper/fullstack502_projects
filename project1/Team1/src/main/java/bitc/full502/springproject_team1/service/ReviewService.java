package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.entity.OrderEntity;
import bitc.full502.springproject_team1.entity.ReviewEntity;

import java.util.List;
import java.util.Map;

public interface ReviewService {
    List<Map<String, Object>> getReviewList(int productId);

    ReviewEntity findById(Integer id);
    List<ReviewEntity> findAll();
    void save(ReviewEntity review);
    void deleteById(Integer id);
    boolean existsByOrderIdx(Integer orderIdx);
    ReviewEntity findByOrderIdx(Integer orderIdx);
    OrderEntity findOrderById(Integer orderIdx);
}
