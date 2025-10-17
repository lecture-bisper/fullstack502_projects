package bitc.full502.projectbq.util;

import bitc.full502.projectbq.domain.entity.item.*;
import bitc.full502.projectbq.domain.entity.user.DeptEntity;
import bitc.full502.projectbq.domain.entity.user.EmpEntity;
import bitc.full502.projectbq.domain.entity.user.RoleEntity;
import bitc.full502.projectbq.domain.entity.user.UserEntity;
import bitc.full502.projectbq.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Util {

    public static UserDto toUserDto(UserEntity user) {
        DeptEntity dept = user.getEmp().getDept();
        EmpEntity emp = user.getEmp();
        RoleEntity role = user.getRole();

        return UserDto.builder()
                .deptId(dept.getId())
                .deptCode(dept.getCode())
                .deptName(dept.getName())
                .empId(emp.getId())
                .empCode(emp.getCode())
                .empName(emp.getName())
                .empEmail(emp.getEmail())
                .empPhone(emp.getPhone())
                .empBirthDate(emp.getBirthDate())
                .empHireDate(emp.getHireDate())
                .userId(user.getId())
                .userStatus(user.getStatus())
                .userCreateDate(user.getCreateDate())
                .roleId(role.getId())
                .roleName(role.getName())
                .roleStockIn(role.getStockIn())
                .roleUpdateMinStock(role.getUpdateMinStock())
                .roleAddItem(role.getAddItem())
                .roleApproveItem(role.getApproveItem())
                .roleUpdateUserInfo(role.getUpdateUserInfo())
                .build();
    }

    public static List<UserDto> toUserDtoList(List<UserEntity> users) {
        return users.stream()
                .map(Util::toUserDto)
                .collect(Collectors.toList());
    }

    public static RoleDto toRoleDto(RoleEntity role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .stockIn(role.getStockIn())
                .updateMinStock(role.getUpdateMinStock())
                .addItem(role.getAddItem())
                .approveItem(role.getApproveItem())
                .updateUserInfo(role.getUpdateUserInfo())
                .build();
    }

    public static List<RoleDto> toRoleDtoList(List<RoleEntity> roles) {
        return roles.stream()
                .map(Util::toRoleDto)
                .collect(Collectors.toList());
    }

    public static StockLogDto toStockLogDto(StockLogEntity stockLog, ItemEntity item, EmpEntity emp) {
        WarehouseEntity warehouse = stockLog.getWarehouse();
        CategoryEntity category = item.getCategory();
        return StockLogDto.builder()
                .id(stockLog.getId())
                .empCode(stockLog.getEmpCode())
                .empName(emp.getName())
                .logDate(stockLog.getLogDate())
                .type(stockLog.getType())
                .quantity(stockLog.getQuantity())
                .memo(stockLog.getMemo())
                .warehouseId(warehouse.getId())
                .warehouseName(warehouse.getName())
                .warehouseKrName(warehouse.getKrName())
                .itemId(item.getId())
                .itemCode(item.getCode())
                .itemName(item.getName())
                .itemManufacturer(item.getManufacturer())
                .itemPrice(item.getPrice())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .categoryKrName(category.getKrName())
                .build();
    }

    public static MinStockDto toMinStockDto(MinStockEntity minEntity) {
        ItemEntity item = minEntity.getItem();
        CategoryEntity category = item.getCategory();
        return MinStockDto.builder()
                .id(minEntity.getId())
                .itemId(item.getId())
                .itemName(item.getName())
                .itemCode(item.getCode())
                .itemManufacturer(item.getManufacturer())
                .itemPrice(item.getPrice())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .categoryKrName(category.getKrName())
                .stockQuantity(0)
                .standardQty(0)
                .safetyQty(0)
                .minStockStatus("")
                .build();
    }

    public static DeptDto toDeptDto(DeptEntity dept) {
        return DeptDto.builder()
                .id(dept.getId())
                .code(dept.getCode())
                .name(dept.getName())
                .build();
    }

    public static List<DeptDto> toDeptDtoList(List<DeptEntity> deptList) {
        return deptList.stream()
                .map(Util::toDeptDto)
                .collect(Collectors.toList());
    }

    public static List<StatisticDto<ItemDto>> filterItemStats(List<StatisticDto<ItemDto>> stats, String codeOrName) {
        return stats.stream()
                .filter(dto -> {
                    if (codeOrName == null || codeOrName.isBlank()) {
                        return true;
                    }
                    String code = dto.getInfo().getCode();
                    String name = dto.getInfo().getName();
                    return (code != null && code.contains(codeOrName)) ||
                            (name != null && name.contains(codeOrName));
                })
                .toList();
    }

    public static List<StatisticDto<UserDto>> filterUserStats(
            List<StatisticDto<UserDto>> stats,
            String empCodeOrName
    ) {
        return stats.stream()
                .filter(dto -> {
                    if (empCodeOrName == null || empCodeOrName.isBlank()) {
                        return true;
                    }
                    UserDto info = dto.getInfo();
                    if (info == null) return false;

                    String code = info.getEmpCode();
                    String name = info.getEmpName();

                    return (code != null && code.contains(empCodeOrName)) ||
                            (name != null && name.contains(empCodeOrName));
                })
                .toList();
    }
}
