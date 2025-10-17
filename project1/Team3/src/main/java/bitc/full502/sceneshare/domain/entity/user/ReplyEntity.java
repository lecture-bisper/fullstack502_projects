package bitc.full502.sceneshare.domain.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reply")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private int replyId;

  @Column(nullable = false)
  private String userId;

  @Column(nullable = false)
  private int boardId;

  @Column
  private String contents;

  @Column
  @CreationTimestamp
  private LocalDateTime createDate;

  @Column
  private LocalDateTime updateDate;
}
