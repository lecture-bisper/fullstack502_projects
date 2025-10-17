package bitc.full502.sceneshare.domain.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieView {

  // 기본
  private Integer movieId;
  private String  movieTitle;

  // 상세에서 쓰는 필드들(템플릿 네임 그대로!)
  private String  movieActors;
  private String  movieDirector;
  private String  movieGenre;        // ✅ 추가
  private String  movieAge;          // ✅ 추가 (OMDb Rated)
  private String  movieCountry;      // ✅ 추가
  private String  movieTime;         // ✅ 추가 (예: "136" 분 문자열)
  private LocalDateTime releaseDate; // ✅ 추가

  // 설명(Plot) — 템플릿에 따라 description/plot 어느 쪽이든 대응
  private String  movieDescription;  // ✅ 추가
  private String  moviePlot;         // (둘 다 둬서 템플릿 호환)

  // 이미지
  private String  posterUrl;         // ✅ 템플릿에서 종종 사용
  private String  moviePosterUrl;    // (메인 페이지 DTO 호환용)
  private String  subTopImgUrl;      // ✅ 템플릿에서 사용

  // 평점/북마크 — 템플릿 호환을 위해 둘 다 제공
  private Double  ratingAvg;         // ✅ 추가
  private Double  movieRatingAvg;    // (메인 페이지 DTO 호환)
  private Long    bookmarkCnt;
}
