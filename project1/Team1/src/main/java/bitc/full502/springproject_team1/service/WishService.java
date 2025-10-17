package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.entity.WishEntity;

import java.util.List;

public interface WishService {

    List<?> findByCustomerId(Integer loginId);

    boolean isWished(Integer loginId, int productId);

    void addWish(Integer loginId, int productId);

    void removeWish(Integer loginId, int productId);
}
