package bitc.full502.movie.service;

import bitc.full502.movie.domain.entity.PreferGenreEntity;

import java.util.List;

public interface PreferGenreService {
    List<String> getPreferGenre(String id, String type) throws Exception;

    void savePreferGenres(List<PreferGenreEntity> genres) throws Exception;

    void truncatePreferGenres(String id) throws Exception;
}
