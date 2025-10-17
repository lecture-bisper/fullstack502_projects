package bitc.full502.springproject_team1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Entity
@Table(name="size")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class SizeEntity {

        @Id
        @Column(name = "size_idx")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer sizeIdx;

        @Column(name = "size_name")
        private String sizeName;

}
