package bitc.full502.movie.service;

import bitc.full502.movie.domain.entity.UserEntity;

import java.util.List;
import java.util.Map;

public interface UserService {
    void updateUserInfo(UserEntity user, Map<String,String> params) throws Exception;
    void updateUserPreferredGenres(UserEntity user, List<String> genreIds)throws Exception;
    void deleteUser(String id) throws Exception;
    void changePassword(UserEntity user, String newPw) throws Exception;
}
