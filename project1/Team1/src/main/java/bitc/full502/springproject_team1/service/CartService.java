package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.entity.CartEntity;

import java.util.List;

public interface CartService {

    List<CartEntity> findByCustomerId(int customerId);

    void addCart(CartEntity cart);  // 저장

    void deleteByCartId(int cartIdx); // 단일 삭제

    void deleteByCustomerId(int customerId);

    void deleteCartItems(int customerIdx, List<Integer> productIds);
}
