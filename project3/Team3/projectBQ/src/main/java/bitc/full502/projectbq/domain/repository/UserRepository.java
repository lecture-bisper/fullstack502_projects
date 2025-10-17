package bitc.full502.projectbq.domain.repository;

import bitc.full502.projectbq.domain.entity.user.EmpEntity;
import bitc.full502.projectbq.domain.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmp(EmpEntity emp);

    boolean existsByEmpAndPwd(EmpEntity emp, String pwd);

    Optional<UserEntity> findByEmp(EmpEntity emp);

    @Query("""
                SELECT u
                FROM UserEntity u
                JOIN u.emp e
                JOIN u.role r
                WHERE (:nameOrEmpCode IS NULL OR e.name LIKE %:nameOrEmpCode% OR e.code LIKE %:nameOrEmpCode%)
                  AND (:deptCode IS NULL OR e.dept.code = :deptCode)
                  AND (:roleName IS NULL OR r.name = :roleName)
                  AND e.hireDate BETWEEN :startDate AND :endDate
            """)
    List<UserEntity> findFilteredUsers(
            @Param("nameOrEmpCode") String nameOrEmpCode,
            @Param("deptCode") String deptCode,
            @Param("roleName") String roleName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
