package bitc.full502.springproject_team1.repository;

import bitc.full502.springproject_team1.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {

    CustomerEntity findByCustomerIdx(int customerIdx);
    CustomerEntity findByCustomerId(String customerId);
    Optional<CustomerEntity> findFirstByCustomerIdAndCustomerEmail(String CustomerId, String CustomerEmail);

    boolean existsByCustomerIdAndCustomerPass(String CustomerId, String CustomerPass);

    boolean existsByCustomerId(String customerId);

}
