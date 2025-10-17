package bitc.full502.springproject_team1.controller;

import bitc.full502.springproject_team1.entity.BoardEntity;
import bitc.full502.springproject_team1.service.BoardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardApiController {

    private final BoardService boardService;

    public BoardApiController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 최근 게시글 3개 반환 API
    @GetMapping("/recent")
    public List<BoardEntity> getRecentBoards() {
        return boardService.findRecentBoards(3);
    }
}

