package bitc.full502.movie.service;

import bitc.full502.movie.domain.entity.UserEntity;

import java.util.Optional;

public interface AuthService {

//    로그인 관련 서비스
     boolean login(String id, String password) throws Exception;
    UserEntity selectUserInfo(String userId) throws Exception;

    Optional<UserEntity> findByNameAndEmail(String name, String email) throws Exception;

    Optional<UserEntity> findByIdAndEmail(String id, String email) throws Exception;



}
