package bitc.full502.springproject_team1.controller;

import bitc.full502.springproject_team1.entity.BoardCommentEntity;
import bitc.full502.springproject_team1.entity.BoardEntity;
import bitc.full502.springproject_team1.entity.CustomerEntity;
import bitc.full502.springproject_team1.repository.BoardCommentRepository;
import bitc.full502.springproject_team1.repository.BoardHeartRepository;
import bitc.full502.springproject_team1.repository.CustomerRepository;
import bitc.full502.springproject_team1.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CustomerRepository customerRepository;
    private final BoardCommentRepository boardCommRepository;
    private final BoardHeartRepository  boardHeartRepository;

    //    =============================mypage 내 board =======================
    // 게시글 상세 페이지
    @GetMapping("/boardDetail{boardIdx}")
    public String boardDetail(@PathVariable Integer boardIdx, Model model) {
        BoardEntity board = boardService.findById(boardIdx);
        model.addAttribute("board", board);
        return "board/boardDetail";
    }


    // 게시글 리스트 페이지
    @GetMapping("/list")
    public String boardList(Model model) {
        List<BoardEntity> boards = boardService.findAll();
        model.addAttribute("boards", boards);
        return "board/list";  // templates/board/list.html
    }

    // 수정 폼 보여주기
    @GetMapping("/update/{boardIdx}")
    public String boardUpdateForm(@PathVariable Integer boardIdx, Model model) {
        BoardEntity board = boardService.findById(boardIdx);
        model.addAttribute("board", board);
        return "board/updateForm";  // templates/board/updateForm.html
    }

    // 수정 저장 처리
    @PostMapping("/update/{boardIdx}")
    public String boardUpdateSave(@PathVariable Integer boardIdx, BoardEntity form) {
        BoardEntity board = boardService.findById(boardIdx);
        board.setBoardPost(form.getBoardPost());
        board.setBoardUploadPhoto(form.getBoardUploadPhoto());
        boardService.save(board);
        return "redirect:/board/list";
    }

    // 게시글 삭제 처리
    @GetMapping("/board/delete/{boardIdx}")
    public String boardDelete(@PathVariable Integer boardIdx) {
        boardService.deleteById(boardIdx);
        return "redirect:/mypage/mywrite";
    }

    //    =============================board 내 board =======================

    //  로그인 세션 불러오는 매서드 페이지 공통 사용
    private Integer getLoginId(HttpSession session) throws IOException {
        Object loginObj = session.getAttribute("loginId");
        if (loginObj instanceof Integer) {
            return (Integer) loginObj;
        } else if (loginObj instanceof String) {
            try {
                return Integer.parseInt((String) loginObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @GetMapping("/boardList")
    public ModelAndView selectBoardList(@RequestParam(name = "sort", defaultValue = "desc") String sort,
                                        HttpSession session) {
        ModelAndView mv = new ModelAndView("board/boardList");

        List<BoardEntity> boardList = boardService.findAllByOrder(sort);
        mv.addObject("boardList", boardList);
        mv.addObject("sort", sort); // 현재 정렬 정보 전달
        return mv;
    }

    // @GetMapping("/boardWrite")
    @GetMapping("/boardWrite")
    public String writeForm() {
        return "board/boardWrite"; // 템플릿 경로: templates/board/boardWrite.html
    }

    @PostMapping("/boardWrite")
    public String savePost(@ModelAttribute BoardEntity board, @RequestParam("imageFile") MultipartFile imageFile, HttpSession session) throws IOException {

        // 세션에서 로그인된 사용자 ID 가져오기
        String customerId = (String) session.getAttribute("customerId");

        if (customerId == null || customerId.isEmpty()) {
            // 로그인하지 않은 상태일 경우 로그인 페이지로 리디렉션
            return "redirect:/login";
        }

        // 해당 ID로 사용자 조회 후 게시글에 설정
        CustomerEntity customer = customerRepository.findByCustomerId(customerId);
        board.setCustomer(customer);

        // 이미지 저장 처리
        String uploadDir = new File("src/main/resources/static/img/board/").getAbsolutePath() + File.separator;
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) uploadFolder.mkdirs();

        if (!imageFile.isEmpty()) {
            String originalFilename = imageFile.getOriginalFilename();

            // 날짜 + 시간 기반 파일명 생성
            String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String safeFilename = timeStamp + "_" + originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

            File destFile = new File(uploadDir + safeFilename);

            imageFile.transferTo(destFile);
            board.setBoardUploadPhoto("/img/board/" + safeFilename);  // DB에 저장될 경로
        } else {
            board.setBoardUploadPhoto("/uploads/default.jpg");
        }

        // 게시글 저장
        boardService.saveBoard(board);
        return "redirect:/boardList";
    }


    @GetMapping("/board/detail/{boardIdx}")
    public ModelAndView boardDetail(@PathVariable("boardIdx") int boardIdx, HttpSession session) {

        ModelAndView mv = new ModelAndView("board/boardDetail");

        // 댓글 포함 조회
        BoardEntity board = boardService.selectBoardWithCommentsById(boardIdx);

        if (board == null) {
            mv.setViewName("error/404"); // 게시글 없을 경우
            return mv;
        }

        mv.addObject("board", board);
        mv.addObject("newComment", new BoardCommentEntity());

        // 로그인 사용자 정보 확인
        String customerId = (String) session.getAttribute("customerId");

        if (customerId != null) {
            CustomerEntity customer = customerRepository.findByCustomerId(customerId);
            mv.addObject("loginCustomer", customer);

            // 좋아요 여부 확인 후 전달
            boolean liked = boardHeartRepository.existsByBoardBoardIdxAndCustomerCustomerIdxAndBoardHeartyn(
                    boardIdx, customer.getCustomerIdx(), "y");
            mv.addObject("liked", liked);
        } else {
            mv.addObject("liked", false); // 비로그인 상태면 false 처리
        }

        return mv;
    }

    @PostMapping("/board/{boardIdx}/comment")
    public String saveComment(@PathVariable int boardIdx,
                              @RequestParam("comment") String commentText,
                              HttpSession session) {

        String customerId = (String) session.getAttribute("customerId");

        if (customerId == null) {
            return "redirect:/login"; // 비로그인 사용자는 로그인 페이지로
        }

        CustomerEntity customer = customerRepository.findByCustomerId(customerId);
        //BoardEntity board = boardService.selectBoardById(boardIdx);
        BoardEntity board = boardService.selectBoardWithCommentsById(boardIdx);

        // 댓글 생성 및 저장
        BoardCommentEntity comment = new BoardCommentEntity();
        comment.setCustomer(customer);
        comment.setBoard(board);
        comment.setBoardComment(commentText);

        boardCommRepository.save(comment);

        // 저장 후 다시 해당 게시글 상세 페이지로 리다이렉트
        return "redirect:/board/detail/" + boardIdx;
    }

    @PostMapping("/board/{boardIdx}/like")
    @ResponseBody
    public int likeBoard(@PathVariable int boardIdx, HttpSession session) {

        Integer customerIdx = (Integer) session.getAttribute("customerIdx");

        if (customerIdx == null) throw new IllegalStateException("로그인이 필요합니다.");


        int updatedCount = boardService.toggleHeart(boardIdx, customerIdx);

        return updatedCount;
    }
}
