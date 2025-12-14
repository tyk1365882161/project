package com.example.shop.repository;

import com.example.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // 查找某个用户购物车里所有的东西
    List<CartItem> findByUserId(Long userId);
    
    // 清空购物车
    void deleteByUserId(Long userId);
}