package bitc.full502.springproject_team1.repository;

import bitc.full502.springproject_team1.entity.CustomerEntity;
import bitc.full502.springproject_team1.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity,Integer> {

    OrderEntity findByOrderIdx(Integer orderIdx);

    List<OrderEntity> findByCustomer(CustomerEntity customer);

    List<OrderEntity> findByCustomer_CustomerIdx(Integer customerIdx);

    List<OrderEntity> findAllByCustomerOrderByOrderDateDesc(CustomerEntity customer);

}

