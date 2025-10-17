package bitc.full502.springproject_team1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="color")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class ColorEntity {

    @Id
    @Column(name = "color_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer colorIdx;

    @Column(name = "color_name")
    private String colorName;
}
