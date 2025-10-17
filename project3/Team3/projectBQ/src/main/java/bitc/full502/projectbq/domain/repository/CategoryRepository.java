package bitc.full502.projectbq.domain.repository;

import bitc.full502.projectbq.domain.entity.item.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
