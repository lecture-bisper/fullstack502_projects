package bitc.full502.sceneshare.domain.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer boardId;

  @Column
  private String userId;

  @Column(name = "user_img")
  private String userImg;

  @Column
  private Integer movieId;

  @Column
  private String title;

  @Column
  private String contents;

  @Column(name = "rating") // DECIMAL(2,1) 쓸 때
  private Double rating;

  // ✅ 추가: 생성일(저장 시점 자동 세팅)
  @CreationTimestamp                // ★ Hibernate가 insert 시 자동으로 현재 시각 주입
  @Column(name = "create_date", nullable = false)
  private LocalDateTime createDate;

  @Column
  private LocalDateTime updateDate;

  @Column(name = "genre")
  private String genre;

  @Column
  private String reply;

  @Transient              // DB 컬럼 만들지 않음
  private Long replyCount;
}
