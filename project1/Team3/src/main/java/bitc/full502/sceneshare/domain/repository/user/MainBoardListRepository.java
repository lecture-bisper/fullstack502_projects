package bitc.full502.sceneshare.domain.repository.user;

import bitc.full502.sceneshare.domain.entity.dto.MovieInfoDTO;
import bitc.full502.sceneshare.domain.entity.user.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainBoardListRepository extends JpaRepository<BoardEntity, Integer> {

}
