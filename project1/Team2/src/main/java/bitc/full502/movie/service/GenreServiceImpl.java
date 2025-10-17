package bitc.full502.movie.service;

import bitc.full502.movie.domain.entity.GenreEntity;
import bitc.full502.movie.domain.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public GenreEntity getGenre(String genreId) throws Exception {
        return genreRepository.findById(genreId).orElse(null);
    }
}
