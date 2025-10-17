package bitc.full502.sceneshare.domain.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private int userIdx;

  @Column(nullable = false, unique = true)
  private String userId;

  @Column(nullable = false)
  private String userPw;

  @Column(nullable = false)
  private String userName;

  @Column
  private String userEmail;

  @Column(nullable = false)
  private String gender;

  @Column
  private String userImg;

  @Column
  private Enum role;

  @Column
  private Integer status;

  @OneToMany(mappedBy = "user")
  private List<BookmarkEntity> bookmarks = new ArrayList<>();
}
