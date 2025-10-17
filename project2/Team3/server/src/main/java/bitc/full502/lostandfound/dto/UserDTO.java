package bitc.full502.lostandfound.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String userId;

    private String phone;

    private String email;

    private String userName;

    private LocalDateTime createDate;

    private String role;

    private boolean autoLogin;
}
