package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.entity.CustomerEntity;

public interface CustomerService {
    CustomerEntity findByCustomerId(String customerId); // ← 이거 꼭 있어야 함
    void updateCustomer(CustomerEntity customer);
    CustomerEntity findById(String customerId);

    CustomerEntity findByIdx(int customerIdx);

    boolean isIdDuplicate(String customerId);
}
