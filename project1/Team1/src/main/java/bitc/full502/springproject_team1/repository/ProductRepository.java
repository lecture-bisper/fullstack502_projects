package bitc.full502.springproject_team1.repository;

import bitc.full502.springproject_team1.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    List<ProductEntity> findByProductNameContainingIgnoreCase(String keyword);
    List<ProductEntity> findAllByOrderByProductId();
    ProductEntity findByProductId(int productId);
    List<ProductEntity> findByProductBrandAndProductCode(String productCode, String productBrand);


    List<ProductEntity> findByProductBrand(String productBrand);

    String productCode(String productCode);

    List<ProductEntity> findByProductCode(String productCode);

    List<ProductEntity> findAllByOrderByProductIdDesc();     // 최신순
    List<ProductEntity> findAllByOrderByProductIdAsc();      // 오래된순
    List<ProductEntity> findAllByOrderByProductPriceDesc();  // 높은 가격순
    List<ProductEntity> findAllByOrderByProductPriceAsc();


    List<ProductEntity> findByProductCodeOrderByProductIdDesc(String category);
    List<ProductEntity> findByProductCodeOrderByProductIdAsc(String category);
    List<ProductEntity> findByProductCodeOrderByProductPriceDesc(String category);
    List<ProductEntity> findByProductCodeOrderByProductPriceAsc(String category);

    List<ProductEntity> findByProductBrandOrderByProductIdAsc(String productBrand);
    List<ProductEntity> findByProductBrandOrderByProductIdDesc(String productBrand);
    List<ProductEntity> findByProductBrandOrderByProductPriceAsc(String productBrand);
    List<ProductEntity> findByProductBrandOrderByProductPriceDesc(String productBrand);


    List<ProductEntity> findTop5ByProductCodeAndProductBrandAndProductIdNotOrderByProductIdDesc(
            String productCode,
            String productBrand,
            Integer productId
    );
}
