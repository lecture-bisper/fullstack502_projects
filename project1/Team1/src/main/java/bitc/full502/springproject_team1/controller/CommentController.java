package bitc.full502.springproject_team1.controller;

import bitc.full502.springproject_team1.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable("commentId") Integer commentId, RedirectAttributes redirectAttributes) {
        commentService.deleteById(commentId);
        redirectAttributes.addFlashAttribute("msg", "댓글이 삭제되었습니다.");
        return "redirect:/mypage/mywrite";
    }
}