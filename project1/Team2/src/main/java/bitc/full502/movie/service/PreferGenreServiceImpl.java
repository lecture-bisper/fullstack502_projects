package bitc.full502.movie.service;

import bitc.full502.movie.domain.entity.PreferGenreEntity;
import bitc.full502.movie.domain.repository.PreferGenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreferGenreServiceImpl implements PreferGenreService {

    private final PreferGenreRepository preferGenreRepository;

    @Override
    public List<String> getPreferGenre(String id, String type) throws Exception {
        return preferGenreRepository.findGenreIdsByUserId(id, type);
    }

    @Override
    public void savePreferGenres(List<PreferGenreEntity> genres) throws Exception {
        preferGenreRepository.saveAll(genres);
    }

    @Override
    public void truncatePreferGenres(String id) throws Exception {
        preferGenreRepository.deleteAllByUserId(id);
    }
}
