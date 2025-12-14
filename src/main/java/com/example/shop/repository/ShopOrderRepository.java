package com.example.shop.repository;

import com.example.shop.entity.ShopOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface ShopOrderRepository extends JpaRepository<ShopOrder, Long> {
    // 查找某个用户的所有历史订单
    List<ShopOrder> findByUserIdOrderByCreateTimeDesc(Long userId);
    // --- 统计最近7天的销售额 ---
    @Query(value = "SELECT DATE_FORMAT(create_time, '%Y-%m-%d') as date, SUM(total_price) as total " +
                   "FROM shop_order " +
                   "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                   "GROUP BY date", nativeQuery = true)
    List<Object[]> findLast7DaysSales();
}