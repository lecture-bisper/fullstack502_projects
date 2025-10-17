package bitc.full502.sceneshare.controller.user;

import bitc.full502.sceneshare.service.user.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class MovieBoardController {

  private final BoardService boardService;

  @PostMapping("/movieDetail/{movieId}/board")
  public String writeBoard(@PathVariable Integer movieId,
                           @RequestParam String title,
                           @RequestParam String contents,
                           @RequestParam Double rating,
                           @RequestParam String genre,
                           HttpSession session) {

    String userId = (String) session.getAttribute("userId");
    if (userId == null) {
      return "redirect:/login";
    }

    boardService.write(movieId, userId, title, contents, rating, genre);

    return "redirect:/movie/detail?movieId=" + movieId;
  }
}
