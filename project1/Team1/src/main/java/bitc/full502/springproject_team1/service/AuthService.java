package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.DTO.CustomerDTO;
import bitc.full502.springproject_team1.entity.CustomerEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface AuthService {
    void joinUser(CustomerDTO cdto);

    int isUserInfo(String customerId, String customerPass) throws Exception;

    CustomerEntity selectUserInfo (String customerId) throws Exception;

    Optional<CustomerEntity> findPassword(String customerId, String customerEmail);

    boolean checkDuplicateId(String customerId);
}
