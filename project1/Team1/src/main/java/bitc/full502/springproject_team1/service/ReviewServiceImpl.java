package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.DTO.ReviewDTO;
import bitc.full502.springproject_team1.entity.CustomerEntity;
import bitc.full502.springproject_team1.entity.OrderEntity;
import bitc.full502.springproject_team1.entity.ProductEntity;
import bitc.full502.springproject_team1.entity.ReviewEntity;
import bitc.full502.springproject_team1.repository.CustomerRepository;
import bitc.full502.springproject_team1.repository.OrderRepository;
import bitc.full502.springproject_team1.repository.ProductRepository;
import bitc.full502.springproject_team1.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, OrderRepository orderRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<Map<String, Object>> getReviewList(int productId) {
        Optional<ProductEntity> optionalProduct = productRepository.findById(productId);
        ProductEntity product = optionalProduct.get();

        if (!optionalProduct.isPresent()) {
            return new ArrayList<>(); }

        List<ReviewEntity> reviewEntityList = reviewRepository.findByProduct(product);
        List<Map<String, Object>> reviewList = new ArrayList<>();

        for (ReviewEntity review : reviewEntityList) {
            OrderEntity order = orderRepository.findById(review.getOrder().getOrderIdx()).orElse(null);
            if (order != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("reviewContent", review.getReviewContent());
                map.put("customerId", order.getCustomer().getCustomerId());
                reviewList.add(map);
            }
        }

        return reviewList;
    }


    @Override
    public ReviewEntity findById(Integer id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰 ID: " + id));
    }

    @Override
    public List<ReviewEntity> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    @Transactional
    public void save(ReviewEntity review) {
        reviewRepository.save(review);
    }

    @Override
    public void deleteById(Integer id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public boolean existsByOrderIdx(Integer orderIdx) {
        return reviewRepository.existsByOrder_OrderIdx(orderIdx);
    }

    @Override
    public ReviewEntity findByOrderIdx(Integer orderIdx) {
        return reviewRepository.findByOrder_OrderIdx(orderIdx);
    }

    @Override
    public OrderEntity findOrderById(Integer orderIdx) {
        return orderRepository.findById(orderIdx).orElse(null);
    }
}
