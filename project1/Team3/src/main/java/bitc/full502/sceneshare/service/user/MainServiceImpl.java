package bitc.full502.sceneshare.service.user;

import bitc.full502.sceneshare.domain.entity.dto.MovieInfoDTO;
import bitc.full502.sceneshare.domain.entity.user.BoardEntity;
import bitc.full502.sceneshare.domain.entity.user.MovieEntity;
import bitc.full502.sceneshare.domain.repository.user.MainBoardListRepository;
import bitc.full502.sceneshare.domain.repository.user.MainRepository;
import bitc.full502.sceneshare.domain.repository.user.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainServiceImpl implements MainService {

  private final MainRepository mainRepository;
  private final MainBoardListRepository mainBoardListRepository;
  private final SearchRepository searchRepository;

  @Override
  public List<MovieInfoDTO> selectBoardListByBookmarkCnt() {
    return mainRepository.findAllByBookmarkCntDesc();
  }

  @Override
  public List<MovieInfoDTO> selectBoardListByReleaseDate() {
    return mainRepository.findAllByReleaseDateDesc();
  }

  @Override
  public List<BoardEntity> selectBoardList() {
    return mainBoardListRepository.findAll();
  }

  @Override
  public Map<String, List<MovieEntity>> movieSearchList(String keyword) {
    Map<String, List<MovieEntity>> result = new HashMap<>();
    result.put("titleList",    searchRepository.findAllByMovieTitleContaining(keyword));
    result.put("directorList", searchRepository.findAllByMovieDirectorContaining(keyword));
    result.put("genreList",    searchRepository.findAllByMovieGenreContaining(keyword));
    return result;
  }
}
