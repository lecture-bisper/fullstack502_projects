package bitc.full502.lostandfound.util;

import bitc.full502.lostandfound.domain.entity.UserEntity;
import bitc.full502.lostandfound.dto.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

public class UserUtil {

    public static UserDTO convertToUserDTO(UserEntity userEntity) {
        return new UserDTO(
                userEntity.getUserId(),
                userEntity.getPhone(),
                userEntity.getEmail(),
                userEntity.getUserName(),
                userEntity.getCreateDate(),
                userEntity.getRole(),
                userEntity.isAutoLogin()
        );
    }

    public static List<UserDTO> convertToUserDTOList(List<UserEntity> userEntityList) {
        return userEntityList.stream()
                .map(UserUtil::convertToUserDTO)
                .collect(Collectors.toList());
    }
}
