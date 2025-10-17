package bitc.full502.springproject_team1.repository;

import bitc.full502.springproject_team1.entity.BoardEntity;
import bitc.full502.springproject_team1.entity.BoardHeartEntity;
import bitc.full502.springproject_team1.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardHeartRepository extends JpaRepository<BoardHeartEntity,Integer> {

    // 해당 게시물 + 고객 조합으로 좋아요 존재 여부 확인
    Optional<BoardHeartEntity> findByBoardAndCustomer(BoardEntity board, CustomerEntity customer);

    // 좋아요 상태가 'y'인 것만 카운트
    long countByBoardAndBoardHeartyn(BoardEntity board, String boardHeartyn);

    boolean existsByBoardBoardIdxAndCustomerCustomerIdxAndBoardHeartyn(int boardIdx, int customerIdx, String yn);
}
