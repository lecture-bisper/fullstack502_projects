package bitc.full502.springproject_team1.repository;

import bitc.full502.springproject_team1.entity.BoardCommentEntity;
import bitc.full502.springproject_team1.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardCommentRepository extends JpaRepository<BoardCommentEntity,Integer> {
    List<BoardCommentEntity> findByCustomer(CustomerEntity customer);

}
