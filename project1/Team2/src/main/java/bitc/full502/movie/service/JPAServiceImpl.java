package bitc.full502.movie.service;

import bitc.full502.movie.domain.entity.UserEntity;
import bitc.full502.movie.domain.repository.FavoritesRepository;
import bitc.full502.movie.domain.repository.GenreRepository;
import bitc.full502.movie.domain.repository.PreferGenreRepository;
import bitc.full502.movie.domain.repository.UserRepository;
import bitc.full502.movie.dto.MediaDTO;
import bitc.full502.movie.dto.MovieDTO;
import bitc.full502.movie.dto.TvDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JPAServiceImpl implements JPAService {

    private final UserRepository userRepository;
    private final PreferGenreRepository preferGenreRepository;
    private final FavoritesRepository favoritesRepository;
    private final GenreRepository genreRepository;

    @Override
    public Boolean duplicateCheck(String userId) throws Exception {
        return userRepository.existsById(userId);
    }

    @Override
    public UserEntity registerUser(UserEntity user) throws Exception {
        return userRepository.save(user);
    }

    @Override
    public String getPreferredGenres(String userId, String type) throws Exception {
        List<String> genreIdStrings = preferGenreRepository.findGenreIdsByUserId(userId, type);

        return genreIdStrings.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    public int[] getFavoriteContentsIds(String id, String type) throws Exception {
        return favoritesRepository.findContentsIdsByUserAndType(id, type).stream()
                .mapToInt(Integer::intValue).toArray();
    }

    @Override
    public List<MediaDTO> convertMovieToMedia(List<MovieDTO> DTOList) throws Exception {
        if (DTOList == null || DTOList.isEmpty()) return new ArrayList<>();
        List<MediaDTO> mediaDTOList = new ArrayList<>();

        for (MovieDTO movieDTO : DTOList) {
            MediaDTO mediaDTO = new MediaDTO();

            mediaDTO.setId(movieDTO.getId());
            mediaDTO.setOverview(movieDTO.getOverview());
            mediaDTO.setPosterPath(movieDTO.getPosterPath());
            mediaDTO.setBackdropPath(movieDTO.getBackdropPath());
            mediaDTO.setPopularity(movieDTO.getPopularity());
            mediaDTO.setVoteAverage(movieDTO.getVoteAverage());
            mediaDTO.setVoteCount(movieDTO.getVoteCount());
            mediaDTO.setGenreIds(movieDTO.getGenreIds());
            mediaDTO.setOriginalLanguage(movieDTO.getOriginalLanguage());
            mediaDTO.setTitle(movieDTO.getTitle());
            mediaDTO.setOriginalTitle(movieDTO.getOriginalTitle());
            mediaDTO.setReleaseDate(movieDTO.getReleaseDate());
            mediaDTO.setAdult(movieDTO.getAdult());
            mediaDTO.setVideo(movieDTO.getVideo());
            mediaDTO.setRuntime(movieDTO.getRuntime());
            mediaDTO.setOriginCountry(movieDTO.getOriginCountry());
            mediaDTO.setType("movie");

            mediaDTOList.add(mediaDTO);
        }

        return mediaDTOList;
    }

    @Override
    public MediaDTO convertMovieToMedia(MovieDTO dto) throws Exception {
        if (dto == null) return new MediaDTO();
        MediaDTO mediaDTO = new MediaDTO();

        mediaDTO.setId(dto.getId());
        mediaDTO.setOverview(dto.getOverview());
        mediaDTO.setPosterPath(dto.getPosterPath());
        mediaDTO.setBackdropPath(dto.getBackdropPath());
        mediaDTO.setPopularity(dto.getPopularity());
        mediaDTO.setVoteAverage(dto.getVoteAverage());
        mediaDTO.setVoteCount(dto.getVoteCount());
        mediaDTO.setGenreIds(dto.getGenreIds());
        mediaDTO.setOriginalLanguage(dto.getOriginalLanguage());
        mediaDTO.setTitle(dto.getTitle());
        mediaDTO.setOriginalTitle(dto.getOriginalTitle());
        mediaDTO.setReleaseDate(dto.getReleaseDate());
        mediaDTO.setAdult(dto.getAdult());
        mediaDTO.setVideo(dto.getVideo());
        mediaDTO.setRuntime(dto.getRuntime());
        mediaDTO.setOriginCountry(dto.getOriginCountry());
        mediaDTO.setType("movie");

        return mediaDTO;
    }

    @Override
    public List<MediaDTO> convertTVToMedia(List<TvDTO> DTOList) throws Exception {
        if (DTOList == null || DTOList.isEmpty()) return new ArrayList<>();
        List<MediaDTO> mediaDTOList = new ArrayList<>();

        for (TvDTO tvDTO : DTOList) {
            MediaDTO mediaDTO = new MediaDTO();

            mediaDTO.setId(tvDTO.getId());
            mediaDTO.setOverview(tvDTO.getOverview());
            mediaDTO.setPosterPath(tvDTO.getPosterPath());
            mediaDTO.setBackdropPath(tvDTO.getBackdropPath());
            mediaDTO.setPopularity(tvDTO.getPopularity());
            mediaDTO.setVoteAverage(tvDTO.getVoteAverage());
            mediaDTO.setVoteCount(tvDTO.getVoteCount());
            mediaDTO.setGenreIds(tvDTO.getGenreIds());
            mediaDTO.setOriginalLanguage(tvDTO.getOriginalLanguage());
            mediaDTO.setTitle(tvDTO.getName());
            mediaDTO.setOriginalTitle(tvDTO.getOriginalName());
            mediaDTO.setReleaseDate(tvDTO.getFirstAirDate());
            mediaDTO.setOriginCountry(tvDTO.getOriginCountry());
            mediaDTO.setNumOfEpisodes(tvDTO.getNumOfEpisodes());
            mediaDTO.setType("tv");

            mediaDTOList.add(mediaDTO);
        }

        return mediaDTOList;
    }

    @Override
    public MediaDTO convertTVToMedia(TvDTO dto) throws Exception {
        if (dto == null) return new MediaDTO();
        MediaDTO mediaDTO = new MediaDTO();

        mediaDTO.setId(dto.getId());
        mediaDTO.setOverview(dto.getOverview());
        mediaDTO.setPosterPath(dto.getPosterPath());
        mediaDTO.setBackdropPath(dto.getBackdropPath());
        mediaDTO.setPopularity(dto.getPopularity());
        mediaDTO.setVoteAverage(dto.getVoteAverage());
        mediaDTO.setVoteCount(dto.getVoteCount());
        mediaDTO.setGenreIds(dto.getGenreIds());
        mediaDTO.setOriginalLanguage(dto.getOriginalLanguage());
        mediaDTO.setTitle(dto.getName());
        mediaDTO.setOriginalTitle(dto.getOriginalName());
        mediaDTO.setReleaseDate(dto.getFirstAirDate());
        mediaDTO.setOriginCountry(dto.getOriginCountry());
        mediaDTO.setNumOfEpisodes(dto.getNumOfEpisodes());
        mediaDTO.setType("tv");

        return mediaDTO;
    }

    @Override
    public String convertGenre(String genreIds) throws Exception {
        String[] genres = genreIds.split(",");
        String[] convertGenres = new String[genres.length];
        for (int i = 0; i < genres.length; i++) {
            convertGenres[i] = genreRepository.findKrNameById(genres[i]);
        }
        return String.join(", ", convertGenres);
    }

}
