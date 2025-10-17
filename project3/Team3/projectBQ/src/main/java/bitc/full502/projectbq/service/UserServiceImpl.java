package bitc.full502.projectbq.service;

import bitc.full502.projectbq.common.Constants;
import bitc.full502.projectbq.domain.entity.user.DeptEntity;
import bitc.full502.projectbq.domain.entity.user.EmpEntity;
import bitc.full502.projectbq.domain.entity.user.RoleEntity;
import bitc.full502.projectbq.domain.entity.user.UserEntity;
import bitc.full502.projectbq.domain.repository.DeptRepository;
import bitc.full502.projectbq.domain.repository.EmpRepository;
import bitc.full502.projectbq.domain.repository.RoleRepository;
import bitc.full502.projectbq.domain.repository.UserRepository;
import bitc.full502.projectbq.dto.*;
import bitc.full502.projectbq.util.Util;
import bitc.full502.projectbq.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmpRepository empRepository;
    private final RoleRepository roleRepository;
    private final DeptRepository deptRepository;

    @Override
    public void joinUser(AuthDto authDto) {
        EmpEntity emp = empRepository.findByCode(authDto.getEmpCode())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        if (userRepository.existsByEmp(emp)) {
            throw new IllegalArgumentException("Already registered");
        }

        UserEntity user = new UserEntity();
        user.setEmp(emp);
        user.setPwd(authDto.getUserPwd());
        user.setRole(roleRepository.findById(1L).get());
        userRepository.save(user);
    }

    @Override
    public String loginUser(AuthDto authDto, boolean autoLogin) {
        EmpEntity emp = empRepository.findByCode(authDto.getEmpCode())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        if (!userRepository.existsByEmpAndPwd(emp, authDto.getUserPwd())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // 만료시간 설정 (기본 1시간, 자동 로그인 시 30일)
        long expireTime = autoLogin ? 60 * 60 * 24 * 30 : 60 * 60;
        return JwtUtil.generateToken(emp.getCode(), expireTime * 1000L);
    }

    @Override
    public UserDto getUserByEmpCode(String empCode) {
        EmpEntity emp = empRepository.findByCode(empCode)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        return Util.toUserDto(userRepository.findByEmp(emp)
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
    }

    @Override
    public void checkPermissionsByEmpCode(String userEmpCode, String permission) {
        EmpEntity emp = empRepository.findByCode(userEmpCode)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        UserEntity user = userRepository.findByEmp(emp)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        RoleEntity role = user.getRole();

        switch (permission) {
            case Constants.PERMISSION_UPDATE_USER_INFO:
                if (role.getUpdateUserInfo() == 'Y') return;
                throw new IllegalArgumentException("Permission denied: " + permission);
            case Constants.PERMISSION_APPROVE_ITEM:
                if (role.getApproveItem() == 'Y' || "MANAGER".equals(role.getName()) || "ADMIN".equals(role.getName())) return;
                throw new IllegalArgumentException("Permission denied: " + permission);
            case Constants.PERMISSION_ADD_ITEM:
                if (role.getAddItem() == 'Y') return;
                throw new IllegalArgumentException("Permission denied: " + permission);
            case Constants.PERMISSION_UPDATE_MIN_STOCK:
                if (role.getUpdateMinStock() == 'Y') return;
                throw new IllegalArgumentException("Permission denied: " + permission);
            case Constants.PERMISSION_STOCK_IN:
                if (role.getStockIn() == 'Y') return;
                throw new IllegalArgumentException("Permission denied: " + permission);
            default:
                throw new IllegalArgumentException("Unknown permission: " + permission);
        }
    }

    @Override
    public void updatePassword(String userEmpCode, @Valid UserPwdDto userPwdDto) {
        EmpEntity emp = empRepository.findByCode(userEmpCode)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        UserEntity user = userRepository.findByEmp(emp)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getPwd().equals(userPwdDto.getCurPassword())) {
            user.setPwd(userPwdDto.getNewPassword());
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Invalid current password");
        }
    }

    @Override
    public List<UserDto> getAllUserByFilter(UserSearchDto userFilter) {
        String nameOrEmpCode = userFilter.getNameOrEmpCode().isEmpty() ? null : userFilter.getNameOrEmpCode();
        String deptCode = userFilter.getDeptCode().isEmpty() ? null : userFilter.getDeptCode();
        String roleName = userFilter.getRoleName().isEmpty() ? null : userFilter.getRoleName();

        List<UserEntity> userList = userRepository.findFilteredUsers(
                nameOrEmpCode,
                deptCode,
                roleName,
                userFilter.getStartDate(),
                userFilter.getEndDate()
        );

        return Util.toUserDtoList(userList);
    }

    @Override
    public void updateInfo(String userEmpCode, UserInfoDto userInfoDto) {
        EmpEntity emp = empRepository.findByCode(userEmpCode)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        UserEntity user = userRepository.findByEmp(emp)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        emp.setEmail(userInfoDto.getEmail());
        emp.setPhone(userInfoDto.getPhone());
        empRepository.save(emp);
    }

    @Override
    public void updateRole(String empCode, String roleName) {
        EmpEntity emp = empRepository.findByCode(empCode)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        UserEntity user = userRepository.findByEmp(emp)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setRole(roleRepository.findByName(roleName));
        userRepository.save(user);
    }

    @Override
    public List<RoleDto> getAllRole() {
        List<RoleEntity> roleList = roleRepository.findAll();
        return Util.toRoleDtoList(roleList);
    }

    @Override
    public List<DeptDto> getAllDept() {
        List<DeptEntity> deptList = deptRepository.findAll();
        return Util.toDeptDtoList(deptList);
    }

    @Override
    public boolean checkPermission(String userEmpCode) {
        EmpEntity emp = empRepository.findByCode(userEmpCode)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        UserEntity user = userRepository.findByEmp(emp)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        RoleEntity role = user.getRole();

        return role.getAddItem() == 'Y';
    }
}
