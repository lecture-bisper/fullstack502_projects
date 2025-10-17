package bitc.full502.movie.domain.repository;

import bitc.full502.movie.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
//  로그인 구현체
    Optional<UserEntity> findByIdAndPassword(String id, String password) throws Exception;

//    회원 탈퇴
    void deleteById(String id);

//    아이디 찾기
    Optional<UserEntity> findByNameAndEmail(String name, String email) throws Exception;
//   비밀번호 찾기
    Optional<UserEntity> findByIdAndEmail(String id, String email) throws Exception;




}
