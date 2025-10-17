package bitc.full502.springproject_team1.repository;

import bitc.full502.springproject_team1.entity.BoardEntity;
import bitc.full502.springproject_team1.entity.CustomerEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<BoardEntity,Integer> {
    List<BoardEntity> findByCustomer(CustomerEntity customer);

    @EntityGraph(attributePaths = {"commentList"})
    Optional<BoardEntity> findWithCommentsByBoardIdx(int boardIdx);

    List<BoardEntity> findAllByOrderByBoardIdxDesc(); // 최신순
    List<BoardEntity> findAllByOrderByBoardIdxAsc();  // 오래된순

}
