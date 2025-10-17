package bitc.full502.projectbq.service;

import bitc.full502.projectbq.dto.*;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService {

    void joinUser(@Valid AuthDto authDto);

    String loginUser(@Valid AuthDto authDto, boolean autoLogin);

    UserDto getUserByEmpCode(String empCode);

    void checkPermissionsByEmpCode(String userEmpCode, String permission);

    void updatePassword(String userEmpCode, @Valid UserPwdDto userPwdDto);

    List<UserDto> getAllUserByFilter(UserSearchDto userFilter);

    void updateInfo(String userEmpCode, UserInfoDto userInfoDto);

    void updateRole(String empCode, String roleName);

    List<RoleDto> getAllRole();

    List<DeptDto> getAllDept();

    boolean checkPermission(String userEmpCode);
}
