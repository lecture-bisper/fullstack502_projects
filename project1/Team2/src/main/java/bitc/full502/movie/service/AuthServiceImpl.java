package bitc.full502.movie.service;


import bitc.full502.movie.domain.entity.UserEntity;
import bitc.full502.movie.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    // 로그인 관련 구현체 추가
    @Override
    public boolean login(String id, String password) throws Exception {
        return userRepository.findByIdAndPassword(id, password).isPresent();
    }

    @Override
    public UserEntity selectUserInfo(String userId) throws Exception {
        return userRepository.findById(userId)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));
    }


//    아이디 찾기
    @Override
    public Optional<UserEntity> findByNameAndEmail(String name, String email) throws Exception {
        return userRepository.findByNameAndEmail(name, email);
    }

//    비밀번호 찾기
    @Override
    public Optional<UserEntity> findByIdAndEmail(String id, String email) throws Exception {
        return userRepository.findByIdAndEmail(id, email);
    }

}

