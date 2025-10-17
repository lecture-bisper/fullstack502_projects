package bitc.full502.movie.service;

import bitc.full502.movie.domain.entity.GenreEntity;
import bitc.full502.movie.domain.entity.PreferGenreEntity;
import bitc.full502.movie.domain.entity.UserEntity;
import bitc.full502.movie.domain.repository.PreferGenreRepository;
import bitc.full502.movie.domain.repository.UserRepository;
import bitc.full502.movie.domain.repository.GenreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PreferGenreRepository preferGenreRepository;
    private final GenreRepository genreRepository;


    @Override
    public void updateUserInfo(UserEntity user, Map<String,String> params) throws Exception {
        if(params.containsKey("name")) {
            user.setName(params.get("name"));
        }
        if(params.containsKey("email")) {
            user.setEmail(params.get("email"));
        }
        String birth = params.get("birthDate");
        if (birth != null && !birth.isBlank()) {
            user.setBirthDate(LocalDate.parse(birth));
        }

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateUserPreferredGenres(UserEntity user, List<String> genreIds) throws  Exception {
        // 기존 선호 장르 삭제
//        preferGenreRepository.deleteByUser(user);
        // 새 선호 장르 저장
        for (String genreId : genreIds) {
            GenreEntity genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new RuntimeException("장르 없음: " + genreId));

            PreferGenreEntity prefer = new PreferGenreEntity();
            prefer.setUser(user);
            prefer.setGenre(genre);
            preferGenreRepository.save(prefer);
        }
    }

    @Transactional
    @Override
    public void deleteUser(String id) throws Exception {
        userRepository.deleteById(id);
    }

    @Override
    public void changePassword(UserEntity user, String newPw) throws Exception {
        user.setPassword(newPw);
        userRepository.save(user);
    }
}

