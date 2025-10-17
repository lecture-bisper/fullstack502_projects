package bitc.full502.springproject_team1.repository;

import bitc.full502.springproject_team1.entity.CartEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartEntity,Integer> {

    @EntityGraph(attributePaths = {"product"}) // ✅ product 조인해서 가져오기
    List<CartEntity> findByCustomerId(int customerId);

    void deleteByCustomerId(int customerId); // 전체삭제

    void deleteByCustomerIdAndProduct_ProductIdIn(Integer customerId, List<Integer> productIds);
}
