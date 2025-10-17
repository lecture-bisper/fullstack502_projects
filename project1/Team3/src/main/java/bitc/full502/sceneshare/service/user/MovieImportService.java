package bitc.full502.sceneshare.service.user;

import bitc.full502.sceneshare.domain.entity.user.MovieEntity;
import bitc.full502.sceneshare.domain.repository.user.MovieRepository;
import bitc.full502.sceneshare.service.OmdbService;
import bitc.full502.sceneshare.service.user.mapper.OmdbToMovieMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieImportService {

  private final OmdbService omdbService;
  private final MovieRepository movieRepository;

  @Transactional
  public MovieEntity importByImdbId(String imdbId) {
    var omdb = omdbService.getByImdbId(imdbId);
    var entity = OmdbToMovieMapper.toEntity(omdb);
    if (entity == null) throw new IllegalStateException("OMDb 응답이 유효하지 않습니다: " + imdbId);

    // ⬇️ 제목+개봉일로 중복 체크
    if (entity.getMovieTitle() != null && entity.getReleaseDate() != null) {
      var found = movieRepository.findByMovieTitleAndReleaseDate(
          entity.getMovieTitle(), entity.getReleaseDate());
      if (found.isPresent()) return found.get();
    }

    return movieRepository.save(entity);
  }
}
