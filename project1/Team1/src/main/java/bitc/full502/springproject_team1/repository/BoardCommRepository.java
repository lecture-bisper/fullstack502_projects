package bitc.full502.springproject_team1.repository;

import bitc.full502.springproject_team1.entity.BoardCommentEntity;
import bitc.full502.springproject_team1.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCommRepository extends JpaRepository<BoardCommentEntity,Integer> {
}
