package bitc.full502.sceneshare.controller.user;

import bitc.full502.sceneshare.domain.entity.user.UserEntity;
import bitc.full502.sceneshare.service.user.UserJoinService;
import bitc.full502.sceneshare.service.user.UserLoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;

@Controller
@RequiredArgsConstructor
public class UserLoginController {

  private final UserLoginService userLoginService;
  private final UserJoinService userJoinService;

  @GetMapping("/user/login.do")
  public String login() {
    return "/user/login/login";
  }


  @RequestMapping("/user/loginProcess.do")
  public String loginProcess(@RequestParam("userId") String userId, @RequestParam("userPw") String userPw, HttpServletRequest req) throws Exception {

    int result = userLoginService.isUserInfo(userId, userPw);

    if (result == 1) {
      UserEntity user = userLoginService.selectUserInfo(userId);

      HttpSession session = req.getSession();
      session.setAttribute("userId", user.getUserId());
      session.setAttribute("userName", user.getUserName());
      session.setAttribute("userEmail", user.getUserEmail());

      return "redirect:/user/loginOK.do";
    }
    else {
      return "redirect:/user/loginFail.do";
    }
  }


  @RequestMapping("/user/loginOK.do")
  public ModelAndView loginOK(HttpServletRequest req) throws Exception {
    ModelAndView mv = new ModelAndView("/user/login/loginOK");

    HttpSession session = req.getSession();

    UserEntity user = new UserEntity();
    user.setUserId((String) session.getAttribute("userId"));
    user.setUserName((String) session.getAttribute("userName"));
    user.setUserEmail((String) session.getAttribute("userEmail"));
    mv.addObject("user", user);

    return mv;
  }

  @RequestMapping("/user/loginFail.do")
  public String loginFail() {
    return "/user/login/loginFail";
  }

  @RequestMapping("/user/logout.do")
  public String logout(HttpServletRequest req) throws Exception {

    HttpSession session = req.getSession();

    session.removeAttribute("userId");
    session.removeAttribute("userName");
    session.removeAttribute("userEmail");

    session.setMaxInactiveInterval(60 * 60 * 3); // 3시간

    session.invalidate();

    return "/user/login/logout";
  }

  @GetMapping("/user/join.do")
  public String join() throws Exception {
    return "/user/login/join";
  }

  @PostMapping("/user/create")
  public void createUser(UserEntity userIdx, @RequestParam("userId") String userId, HttpServletResponse resp) throws Exception {

    int result = userLoginService.isUserId(userId);

    if (result == 0) {
      userJoinService.newUser(userIdx);

      resp.setContentType("text/html;charset=UTF-8");
      PrintWriter writer = resp.getWriter();

      String script = "<script>";
//            script += "alert('회원가입이 완료되었습니다.');";  // 회원가입 완료 후 페이지 변경
      script += "location.href='/user/joinSuccess.do';";
      script += "</script>";
      writer.print(script);
    }
    else {
      resp.setContentType("text/html;charset=UTF-8");
      PrintWriter writer = resp.getWriter();

      String script = "<script>";
      script += "alert('중복되는 아이디입니다.');";
      script += "location.href='/user/join.do';";
      script += "</script>";
      writer.print(script);
    }
  }

  // jin 추가
  @GetMapping("/user/joinSuccess.do")
  public String joinSuccess() throws Exception {
    return "/user/login/joinSuccess";
  }

  @GetMapping("/user/myPage.do")
  public String myPage() throws Exception {
    return "/user/login/myPage";
  }

  @GetMapping("/user/myUpdate.do")
  public String myUpdate() throws Exception {
    return "/user/login/myUpdate";
  }
}
