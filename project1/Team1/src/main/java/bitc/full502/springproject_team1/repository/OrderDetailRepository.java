package bitc.full502.springproject_team1.repository;

import bitc.full502.springproject_team1.entity.OrderEntity;

import bitc.full502.springproject_team1.entity.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Integer> {
    Optional<OrderDetailEntity> findByOrder_OrderIdx(int orderIdx);

//    List<OrderDetailEntity> findByOrderIn(List<OrderEntity> orders);

    @Query("SELECT d FROM OrderDetailEntity d JOIN FETCH d.product JOIN FETCH d.order WHERE d.order IN :orders")
    List<OrderDetailEntity> findByOrderIn(@Param("orders") List<OrderEntity> orders);

    List<OrderDetailEntity> findByOrder(OrderEntity order);
}
