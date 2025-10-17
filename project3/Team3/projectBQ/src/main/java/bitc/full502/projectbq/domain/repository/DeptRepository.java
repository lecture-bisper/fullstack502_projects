package bitc.full502.projectbq.domain.repository;

import bitc.full502.projectbq.domain.entity.user.DeptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeptRepository extends JpaRepository<DeptEntity, Long> {
}
