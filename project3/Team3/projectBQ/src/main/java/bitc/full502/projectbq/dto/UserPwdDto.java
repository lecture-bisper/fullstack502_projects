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
public class UserPwdDto {

    @NotBlank(message = "Current password is required")
    private String curPassword;
    @NotBlank(message = "New password is required")
    private String newPassword;
}
