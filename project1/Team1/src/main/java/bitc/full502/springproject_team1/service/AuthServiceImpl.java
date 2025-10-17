package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.DTO.CustomerDTO;
import bitc.full502.springproject_team1.entity.CustomerEntity;
import bitc.full502.springproject_team1.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;
    public AuthServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public boolean checkDuplicateId(String customerId) {
        return customerRepository.existsByCustomerId(customerId);
    }
    //===============================================================================================================
    //==================================================로그인=======================================================
    @Override
    public void joinUser(CustomerDTO cdto) {
        CustomerEntity cent = new CustomerEntity();
        cent.setCustomerName(cdto.getCustomerName());
        cent.setCustomerId(cdto.getCustomerId());
        cent.setCustomerPass(cdto.getCustomerPass());
        cent.setCustomerEmail(cdto.getCustomerEmail());
        cent.setCustomerAddr(cdto.getCustomerAddr());
        cent.setCustomerPhone(cdto.getCustomerPhone());
        cent.setCustomerCoupon_yn("Y");
        customerRepository.save(cent);
    }


    //===============================================================================================================
    //==================================================회원가입=======================================================

    //  사용자 존재 여부 확인
    @Override
    public int isUserInfo(String customerId, String customerPass)  throws Exception{
        boolean result = customerRepository.existsByCustomerIdAndCustomerPass(customerId, customerPass);
        if (result) {
            return 1;
        }
        return 0;
    }
    //  사용자 정보 가져오기
    @Override
    public CustomerEntity selectUserInfo(String customerId) throws Exception {
        return customerRepository.findByCustomerId(customerId);
    }


    //===============================================================================================================
    //==================================================비밀번호 찾기=======================================================
    @Override
    public Optional<CustomerEntity> findPassword(String customerId, String customerEmail) {
        return customerRepository.findFirstByCustomerIdAndCustomerEmail(customerId, customerEmail);
    }
}
