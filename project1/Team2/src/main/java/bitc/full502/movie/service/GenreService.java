package bitc.full502.movie.service;

import bitc.full502.movie.domain.entity.GenreEntity;


public interface GenreService {

    GenreEntity getGenre(String genreIds) throws Exception;
}
