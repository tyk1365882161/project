package com.example.shop.repository;

import com.example.shop.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 如果以后你想根据分类查商品，可以在这里加：
    List<Product> findByNameContainingIgnoreCase(String keyword);
}
