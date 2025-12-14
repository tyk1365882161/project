package com.example.shop.repository;

import com.example.shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 这里定义一个方法，JPA会自动帮你写SQL：select * from user where username = ?
    User findByUsername(String username);
}