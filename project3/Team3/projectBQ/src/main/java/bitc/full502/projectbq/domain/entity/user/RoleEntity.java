package bitc.full502.projectbq.domain.entity.user;

import bitc.full502.projectbq.common.Constants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @Builder.Default
    private String name = Constants.ROLE_NAME_USER;

    @Column(nullable = false)
    @Builder.Default
    private char stockIn = 'N';

    @Column(nullable = false)
    @Builder.Default
    private char updateMinStock = 'N';

    @Column(nullable = false)
    @Builder.Default
    private char addItem = 'N';

    @Column(nullable = false)
    @Builder.Default
    private char approveItem = 'N';

    @Column(nullable = false)
    @Builder.Default
    private char updateUserInfo = 'N';
}
