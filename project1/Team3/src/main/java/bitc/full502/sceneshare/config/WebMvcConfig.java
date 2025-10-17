package bitc.full502.sceneshare.config;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfig  implements WebMvcConfigurer {
  //  @Override
//  public void addInterceptors(InterceptorRegistry registry) {
//    registry.addInterceptor(new LoginCheck())
//        .addPathPatterns("/user/**")
//        .addPathPatterns("/main/**")
//        .addPathPatterns("/bookmarks/**")
//        .excludePathPatterns("/user/login.do")
//        .excludePathPatterns("/user/loginProcess.do")
//        .excludePathPatterns("/user/loginFail.do")
//        .excludePathPatterns("/user/logout.do")
//        .excludePathPatterns("/user/join.do")
//        .excludePathPatterns("/user/joinSuccess.do") // jin 추가
//        .excludePathPatterns("/main")
//        .excludePathPatterns("/user/create")
//        .excludePathPatterns("/main/search")
//        .excludePathPatterns("/user/searchResult")
//        // 추가
////                .addPathPatterns("/movieDetail/**")
//        .excludePathPatterns("/main/movieListBookmarkCnt")
//        .excludePathPatterns("/main/movieListReleaseDate")
//        .excludePathPatterns("/main/boardList")
//        .excludePathPatterns("/user/noticeDetail.do");
//
//  }
}
