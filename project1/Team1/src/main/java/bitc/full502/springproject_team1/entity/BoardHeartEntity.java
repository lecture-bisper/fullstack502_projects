package bitc.full502.springproject_team1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="board_heart")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class BoardHeartEntity {

    @Id
    @Column(name = "heart_idx", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer boardHeartIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id_heart")
    private CustomerEntity customer;
//    원래 customerId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b_idx_heart")
    private BoardEntity board;
//    원래 boardId

    @Column(name="heart_yn", nullable = false, length = 45)
    private String boardHeartyn;

    public String getBoardHeartyn() {
        return boardHeartyn;
    }

    public void setBoardHeartyn(String heartYn) {
        this.boardHeartyn = heartYn;
    }

}