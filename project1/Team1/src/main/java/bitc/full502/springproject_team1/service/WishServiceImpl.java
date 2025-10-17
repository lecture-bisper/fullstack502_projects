package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.entity.ProductEntity;
import bitc.full502.springproject_team1.entity.WishEntity;
import bitc.full502.springproject_team1.repository.ProductRepository;
import bitc.full502.springproject_team1.repository.ReviewRepository;
import bitc.full502.springproject_team1.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishServiceImpl implements WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public List<?> findByCustomerId(Integer loginId) {
       return wishRepository.findByCustomerId(loginId);
    }

    @Override
    public boolean isWished(Integer loginId, int productId) {
        return wishRepository.existsByCustomerIdAndProduct_ProductId(loginId, productId);
    }

    @Override
    public void addWish(Integer loginId, int productId) {
// 중복 체크 (가장 중요!)
        if (wishRepository.existsByCustomerIdAndProduct_ProductId(loginId, productId)) {
            return;
        }

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

        WishEntity wish = new WishEntity();
        wish.setCustomerId(loginId);
        wish.setProduct(product);
        wish.setWishCheck(1);

        wishRepository.save(wish);
    }

    @Override
    public void removeWish(Integer loginId, int productId) {
        Optional<WishEntity> existing = wishRepository.findByCustomerIdAndProduct_ProductId(loginId, productId);
        existing.ifPresent(wishRepository::delete);
    }
}
