package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.entity.CartEntity;
import bitc.full502.springproject_team1.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Override
    public List<CartEntity> findByCustomerId(int customerId) {
        return cartRepository.findByCustomerId(customerId);
    }

    @Override
    public void addCart(CartEntity cart) {
        cartRepository.save(cart);
    }

    @Override
    public void deleteByCartId(int cartIdx) {
        cartRepository.deleteById(cartIdx);
    }

    @Override
    @Transactional
    public void deleteByCustomerId(int customerId) {
        cartRepository.deleteByCustomerId(customerId);
    }

    @Override
    @Transactional
    public void deleteCartItems(int customerIdx, List<Integer> productIds) {
        cartRepository.deleteByCustomerIdAndProduct_ProductIdIn(customerIdx, productIds);
    }

    }

