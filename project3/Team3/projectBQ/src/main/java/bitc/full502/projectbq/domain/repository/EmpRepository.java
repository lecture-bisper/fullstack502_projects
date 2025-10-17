package bitc.full502.projectbq.domain.repository;

import bitc.full502.projectbq.domain.entity.user.EmpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpRepository extends JpaRepository<EmpEntity, Long> {

    Optional<EmpEntity> findByCode(String code);
}
