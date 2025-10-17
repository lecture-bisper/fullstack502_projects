package bitc.full502.projectbq.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AuthDto {
// 로그인 회원가입

    @NotBlank(message = "Employee code is required")
    private String empCode;
    @NotBlank(message = "Password is required")
    private String userPwd;
}
