package bitc.full502.springproject_team1.entity;

import bitc.full502.springproject_team1.entity.BoardCommentEntity;
import bitc.full502.springproject_team1.entity.CustomerEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="board")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"customer", "commentList"})
public class BoardEntity {

    @Id
    @Column(name = "b_idx", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer boardIdx;

    @Column(name="post", nullable = false, length = 500)
    private String boardPost;

    @Column(name="h_count")
    private int boardHeartCount;

    @Column(name="r_count")
    private int boardReplyCount;

    @Column(name="b_upload_photo", nullable = false, length = 500)
    private String boardUploadPhoto;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "c_id_board")  // 외래키 이름
    private CustomerEntity customer;
//    원래 customerId

    // 이거 추가 했어요
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<BoardCommentEntity> commentList = new ArrayList<>();

}