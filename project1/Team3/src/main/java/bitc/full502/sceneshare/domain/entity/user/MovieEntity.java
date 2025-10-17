package bitc.full502.sceneshare.domain.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private int movieId;

  @Column(nullable = false)
  private String movieTitle;

  @Column(nullable = false)
  private String movieGenre;

  @Column(nullable = false)
  private String movieDirector;

  @Column(nullable = false)
  private String movieDescription;

  @Column(nullable = false)
  private String posterUrl;

  @Column(nullable = false)
  private LocalDateTime releaseDate;

  @Column
  private Double ratingAvg; // Integer -> Double로 변경(jn)

  // jin 추가
  @Column(nullable = false)
  private String subTopImgUrl;

  @Column
  private String movieCountry;

  @Column
  private String movieTime;

  @Column
  private String movieAge;

  @Column
  private String movieActors;
  // jin 추가 end

  @OneToMany(mappedBy = "movie")
  private List<BookmarkEntity> bookmarks = new ArrayList<>();
}
