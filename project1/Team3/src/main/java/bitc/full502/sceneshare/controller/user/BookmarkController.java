package bitc.full502.sceneshare.controller.user;

import bitc.full502.sceneshare.service.user.BookmarkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

  private final BookmarkService bookmarkService;

  @PostMapping("/likePost/{movieId}")
  public Object likePost(@PathVariable("movieId") int movieId, HttpServletRequest req) throws Exception {

    HttpSession session = req.getSession();

    String userId = (String) session.getAttribute("userId");

    System.out.println("테스트..  /  받은 번호 : " + movieId + "\t사용자 ID : " + userId);

    bookmarkService.likePost(movieId, userId);

    return "success";
  }

  @DeleteMapping("/unLikesPost/{movieId}")
  public Object unlikePost(@PathVariable("movieId") int movieId, HttpServletRequest req) throws Exception {

    HttpSession session = req.getSession();

    String userId = (String) session.getAttribute("userId");

    System.out.println("테스트..  /  받은 번호 : " + movieId + "\t사용자 ID : " + userId);

    bookmarkService.unLikePost(movieId, userId);

    return "delete success";
  }
}
