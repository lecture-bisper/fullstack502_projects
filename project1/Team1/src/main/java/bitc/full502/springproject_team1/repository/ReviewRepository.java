package bitc.full502.springproject_team1.repository;

import bitc.full502.springproject_team1.entity.ProductEntity;
import bitc.full502.springproject_team1.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity , Integer> {
    List<ReviewEntity> findByProduct(ProductEntity product);


    // orderIdx 리스트에 포함된 리뷰들 조회
    List<ReviewEntity> findByOrder_OrderIdxIn(List<Integer> orderIdxList);

    // 특정 orderIdx에 해당하는 리뷰 존재 여부 체크
    boolean existsByOrder_OrderIdx(Integer orderIdx);

    // 특정 orderIdx에 해당하는 리뷰 한 건 조회
    ReviewEntity findByOrder_OrderIdx(Integer orderIdx);
}
