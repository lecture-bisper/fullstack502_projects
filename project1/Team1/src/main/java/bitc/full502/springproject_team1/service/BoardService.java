package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.entity.BoardEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface BoardService {
    BoardEntity findById(Integer boardIdx);

    List<BoardEntity> findAll();

    void save(BoardEntity board);

    void deleteById(Integer boardIdx);

    List<BoardEntity> selectBoardList();

    void saveBoard(BoardEntity board);

    BoardEntity selectBoardWithCommentsById(int boardIdx);

    int toggleHeart(int boardIdx, Integer customerIdx);

    boolean hasUserLikedBoard(int boardIdx, int customerIdx);

    List<BoardEntity> findAllByOrder(String sort);

    //달빛 추가
    List<BoardEntity> findRecentBoards(int count);

}
