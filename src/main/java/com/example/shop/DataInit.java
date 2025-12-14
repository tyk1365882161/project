package com.example.shop;

import com.example.shop.entity.Product;
import com.example.shop.entity.User;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInit {

    @Bean
    public CommandLineRunner initData(UserRepository userRepo, ProductRepository productRepo) {
        return args -> {
            // 1. 检查有没有用户，没有就创建
            if (userRepo.count() == 0) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("123456"); // 真实项目请加密
                admin.setEmail("admin@test.com");
                admin.setRole("ADMIN");
                userRepo.save(admin);

                User user = new User();
                user.setUsername("user");
                user.setPassword("123456");
                user.setEmail("user@test.com");
                user.setRole("CUSTOMER");
                userRepo.save(user);
                System.out.println(">>> 初始化用户数据完成：admin/123456, user/123456");
            }

            // 2. 检查有没有商品，没有就创建
            if (productRepo.count() == 0) {
                Product p1 = new Product();
                p1.setName("iPhone 15 Pro");
                p1.setPrice(9999.0);
                p1.setDescription("钛金属设计，A17 Pro 芯片，超强摄像系统。");
                p1.setStock(10);
                p1.setImageUrl("https://images.unsplash.com/photo-1695048133142-1a20484d2569?auto=format&fit=crop&w=500&q=80");
                productRepo.save(p1);

                Product p2 = new Product();
                p2.setName("MacBook Air");
                p2.setPrice(8500.0);
                p2.setDescription("轻薄便携，M2 芯片，超长续航。");
                p2.setStock(5);
                p2.setImageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca4?auto=format&fit=crop&w=500&q=80");
                productRepo.save(p2);
                
                Product p3 = new Product();
                p3.setName("Sony WH-1000XM5");
                p3.setPrice(2299.0);
                p3.setDescription("行业领先的主动降噪无线耳机。");
                p3.setStock(20);
                p3.setImageUrl("https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?auto=format&fit=crop&w=500&q=80");
                productRepo.save(p3);
                
                System.out.println(">>> 初始化商品数据完成");
            }
        };
    }
}