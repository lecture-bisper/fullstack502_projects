package bitc.full502.projectbq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    // 부서
    private long deptId;
    private String deptCode;
    private String deptName;

    // 사원
    private long empId;
    private String empCode;
    private String empName;
    private String empEmail;
    private String empPhone;
    private LocalDate empBirthDate;
    private LocalDate empHireDate;

    // 사용자
    private long userId;
    private String userStatus;
    private LocalDateTime userCreateDate;

    // 권한
    private long roleId;
    private String roleName;
    private char roleStockIn;
    private char roleUpdateMinStock;
    private char roleAddItem;
    private char roleApproveItem;
    private char roleUpdateUserInfo;
}
