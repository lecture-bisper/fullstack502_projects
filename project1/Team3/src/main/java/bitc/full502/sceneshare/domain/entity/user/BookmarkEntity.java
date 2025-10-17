package bitc.full502.sceneshare.domain.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bookmark")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private int bookmarkId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "movie_id")
  private MovieEntity movie;
}
