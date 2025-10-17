package bitc.full502.springproject_team1.repository;

import bitc.full502.springproject_team1.entity.WishEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<WishEntity,Integer> {
    Optional<WishEntity> findByProduct_ProductIdAndCustomerId(Integer productId, Integer customerId);

    Optional<WishEntity> findByCustomerIdAndProduct_ProductId(Integer loginId, int productId);

    @EntityGraph(attributePaths = "product")
    Optional<WishEntity> findByCustomerIdAndProduct_ProductId(int customerId, int productId);

    List<WishEntity> findByCustomerId(int customerId);

    boolean existsByCustomerIdAndProduct_ProductId(int customerId, int productId);
}
