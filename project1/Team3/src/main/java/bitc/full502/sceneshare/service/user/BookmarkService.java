package bitc.full502.sceneshare.service.user;

import bitc.full502.sceneshare.domain.entity.user.BookmarkEntity;
import bitc.full502.sceneshare.domain.entity.user.MovieEntity;
import bitc.full502.sceneshare.domain.entity.user.UserEntity;
import bitc.full502.sceneshare.domain.repository.user.BookmarkRepository;
import bitc.full502.sceneshare.domain.repository.user.MovieDetailRepository;
import bitc.full502.sceneshare.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookmarkService {

  private final UserRepository userRepository;
  private final MovieDetailRepository movieDetailRepository;
  private final BookmarkRepository bookmarkRepository;

  public void likePost(int movieId, String userId) throws Exception {

    UserEntity user = userRepository.findByUserId(userId);
    MovieEntity movie = movieDetailRepository.findByMovieId(movieId);

    BookmarkEntity bookmark = new BookmarkEntity();

    bookmark.setUser(user);

    bookmark.setMovie(movie);

    bookmarkRepository.save(bookmark);
  }

  public void unLikePost(int movieId, String userId) throws Exception {

    UserEntity user = userRepository.findByUserId(userId);
    MovieEntity movie = movieDetailRepository.findByMovieId(movieId);

    BookmarkEntity bookmark = new BookmarkEntity();
    bookmark.setUser(user);
    bookmark.setMovie(movie);

    bookmarkRepository.delete(bookmark);
  }
}
