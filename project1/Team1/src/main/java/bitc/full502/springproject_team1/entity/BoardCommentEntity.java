package bitc.full502.springproject_team1.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="board_comment")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"customer", "board"})
public class BoardCommentEntity {

    @Id
    @Column(name = "board_comm_idx", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer boardCommIdx;

    // CustomerEntity와 ManyToOne 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "c_id_comm")
    private CustomerEntity customer;
//    원래 customerId

    @Column(name="comment", nullable = false, length = 500)
    private String boardComment;


    // 게시글(보드)와 ManyToOne 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "b_idx_comm")
    private BoardEntity board;
//    원래 boardIdx



}
