package bitc.full502.sceneshare.service.user;

import bitc.full502.sceneshare.domain.entity.dto.MovieInfoDTO;
import bitc.full502.sceneshare.domain.entity.user.BoardEntity;
import bitc.full502.sceneshare.domain.entity.user.MovieEntity;

import java.util.List;
import java.util.Map;

public interface MainService {

  List<MovieInfoDTO> selectBoardListByBookmarkCnt() throws Exception;

  List<MovieInfoDTO> selectBoardListByReleaseDate() throws Exception;

  List<BoardEntity> selectBoardList() throws Exception;

  Map<String, List<MovieEntity>> movieSearchList(String searchMovie) throws Exception;
}
