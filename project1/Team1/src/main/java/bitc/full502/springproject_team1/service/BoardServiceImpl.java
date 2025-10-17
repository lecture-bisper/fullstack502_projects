package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.entity.BoardEntity;
import bitc.full502.springproject_team1.entity.BoardHeartEntity;
import bitc.full502.springproject_team1.entity.CustomerEntity;
import bitc.full502.springproject_team1.repository.BoardHeartRepository;
import bitc.full502.springproject_team1.repository.BoardRepository;
import bitc.full502.springproject_team1.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {


    private final BoardRepository boardRepository;
    private final CustomerRepository customerRepository;
    private final BoardHeartRepository boardHeartRepository;

    // 게시글 단건 조회
    public BoardEntity findById(Integer id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));
    }

    // 게시글 전체 조회
    public List<BoardEntity> findAll() {
        return boardRepository.findAll();
    }

    // 게시글 저장 또는 수정 (ID가 있으면 수정, 없으면 새 등록)
    public void save(BoardEntity board) {
        boardRepository.save(board);
    }

    // 게시글 삭제
    public void deleteById(Integer id) {
        boardRepository.deleteById(id);
    }

    @Override
    public List<BoardEntity> selectBoardList() {
        return boardRepository.findAll();
    }

    @Override
    public void saveBoard(BoardEntity board) {
        boardRepository.save(board);
    }

    @Override
    public BoardEntity selectBoardWithCommentsById(int boardIdx) {

        return boardRepository.findWithCommentsByBoardIdx(boardIdx).orElse(null);
    }

    @Override
    public int toggleHeart(int boardIdx, Integer customerIdx) {


        // 게시물 조회
        BoardEntity board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        // 고객 조회 (customerIdx 기준)
        CustomerEntity customer = customerRepository.findById(customerIdx)
                .orElseThrow(() -> new IllegalArgumentException("고객이 존재하지 않습니다."));

        // 좋아요 기록 확인
        Optional<BoardHeartEntity> optionalHeart = boardHeartRepository.findByBoardAndCustomer(board, customer);

        if (optionalHeart.isPresent()) {
            BoardHeartEntity heart = optionalHeart.get();
            // 토글: y → n, n → y
            heart.setBoardHeartyn(heart.getBoardHeartyn().equals("y") ? "n" : "y");
            boardHeartRepository.save(heart);
        } else {
            // 새로 좋아요 등록
            BoardHeartEntity newHeart = new BoardHeartEntity();
            newHeart.setBoard(board);
            newHeart.setCustomer(customer);
            newHeart.setBoardHeartyn("y");
            boardHeartRepository.save(newHeart);
        }

        // 좋아요 수 다시 계산
        long count = boardHeartRepository.countByBoardAndBoardHeartyn(board, "y");
        board.setBoardHeartCount((int) count);
        boardRepository.save(board);

        return (int) count;
    }

    @Override
    public boolean hasUserLikedBoard(int boardIdx, int customerIdx) {
        return boardHeartRepository.existsByBoardBoardIdxAndCustomerCustomerIdxAndBoardHeartyn(
                boardIdx, customerIdx, "y"
        );
    }

    // BoardServiceImpl.java
    @Override
    public List<BoardEntity> findAllByOrder(String sort) {
        if ("asc".equalsIgnoreCase(sort)) {
            return boardRepository.findAllByOrderByBoardIdxAsc();
        } else {
            return boardRepository.findAllByOrderByBoardIdxDesc(); // 기본 최신순
        }
    }

    //달빛 추가
    @Override
    public List<BoardEntity> findRecentBoards(int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "boardIdx"));  // 또는 createdAt
        return boardRepository.findAll(pageable).getContent();
    }


}