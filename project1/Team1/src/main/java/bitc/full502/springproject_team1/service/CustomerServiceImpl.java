package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.entity.CustomerEntity;
import bitc.full502.springproject_team1.repository.CustomerRepository;
import bitc.full502.springproject_team1.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public boolean isIdDuplicate(String customerId) {
        return customerRepository.existsByCustomerId(customerId);
    }

    // 🔍 ID로 사용자 조회 (로그인용)
    @Override
    public CustomerEntity findByCustomerId(String customerId) {
        return customerRepository.findByCustomerId(customerId);
    }

    // 💾 사용자 정보 업데이트
    @Override
    @Transactional
    public void updateCustomer(CustomerEntity customer) {
        customerRepository.save(customer); // 변경 감지 → JPA 저장
    }

    @Override
    public CustomerEntity findById(String customerId) {
        return customerRepository.findByCustomerId(customerId);
    }

    @Override
    public CustomerEntity findByIdx(int customerIdx) {
        return customerRepository.findByCustomerIdx(customerIdx);
    }

}
