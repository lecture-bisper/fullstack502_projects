package bitc.full502.movie.controller;

import bitc.full502.movie.domain.entity.FavoritesEntity;
import bitc.full502.movie.domain.entity.GenreEntity;
import bitc.full502.movie.domain.entity.PreferGenreEntity;
import bitc.full502.movie.domain.entity.UserEntity;
import bitc.full502.movie.domain.repository.GenreRepository;
import bitc.full502.movie.domain.repository.PreferGenreRepository;
import bitc.full502.movie.domain.repository.UserRepository;
import bitc.full502.movie.dto.PreferGenreDTO;
import bitc.full502.movie.service.FavoritesService;
import bitc.full502.movie.service.GenreService;
import bitc.full502.movie.service.PreferGenreService;
import bitc.full502.movie.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final PreferGenreService preferGenreService;
    private final GenreService genreService;
    private final FavoritesService favoritesService;
    private final UserRepository userRepository;

    @PostMapping("/profile/genres")
    @ResponseBody
    public String saveUserGenres(@RequestBody List<PreferGenreDTO> genreList, HttpSession session) throws Exception {
        UserEntity user = (UserEntity) session.getAttribute("loginUser");
        List<PreferGenreEntity> preferGenreEntity = new ArrayList<>();

        for (PreferGenreDTO dto : genreList) {
            String type = dto.getType();

            for (Integer genreId : dto.getGenreIds()) {
                GenreEntity genre = genreService.getGenre(String.valueOf(genreId)); // 단일 genreId를 넘김
                PreferGenreEntity entity = new PreferGenreEntity();
                entity.setType(type);
                entity.setGenre(genre);
                entity.setUser(user);
                preferGenreEntity.add(entity);
            }
        }

        preferGenreService.truncatePreferGenres(user.getId());
        preferGenreService.savePreferGenres(preferGenreEntity);
        return "";
    }

    @PostMapping("/bookmark")
    @ResponseBody
    public String saveUserBookmarks(@RequestParam("type") String type,
                                    @RequestParam("contentId") String contentId, HttpSession session) throws Exception {
        UserEntity user = (UserEntity) session.getAttribute("loginUser");
        FavoritesEntity favorite = new FavoritesEntity();

        favorite.setUser(user);
        favorite.setType(type);
        favorite.setContentsId(Integer.parseInt(contentId));

        favoritesService.insertBookmark(favorite);
        return "";
    }

    @DeleteMapping("/bookmark")
    @ResponseBody
    public String deleteUserBookmarks(@RequestParam("type") String type,
                                      @RequestParam("contentId") String contentId, HttpSession session) throws Exception {
        UserEntity user = (UserEntity) session.getAttribute("loginUser");

        favoritesService.deleteBookmark(user.getId(), type, contentId);
        return "";
    }

    @PutMapping("/userUpdate")
    public ResponseEntity<String> updateUser(
            @RequestBody Map<String, String> data,
            HttpSession session) {

        UserEntity user = (UserEntity) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인 필요");
        }

        user.setEmail(data.get("email"));
        user.setBirthDate(LocalDate.parse(data.get("birthDate")));
        user.setName(data.get("name"));

        userRepository.save(user);  // 갱신

        return ResponseEntity.ok("ok");
    }

    @PutMapping("/imgUpdate")
    @ResponseBody
    public ResponseEntity<String> updateProfileImage(@RequestBody Map<String, String> data, HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인 필요");
        }

        String profileImg = data.get("profileImg");
        if (profileImg == null || profileImg.isEmpty()) {
            return ResponseEntity.badRequest().body("프로필 이미지 데이터가 없습니다.");
        }

        user.setProfileImg(profileImg);
        userRepository.save(user);

        return ResponseEntity.ok("프로필 이미지가 업데이트 되었습니다.");
    }
}
