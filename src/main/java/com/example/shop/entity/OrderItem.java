package com.example.shop.entity;

import jakarta.persistence.*;

@Entity
public class OrderItem {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ShopOrder getShopOrder() {
        return shopOrder;
    }

    public void setShopOrder(ShopOrder shopOrder) {
        this.shopOrder = shopOrder;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String productName; // 快照：防止商品改名后订单看不懂
    private Double price;       // 快照：下单时的价格
    private Integer quantity;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private ShopOrder shopOrder; // 属于哪个订单
    
    // 请生成 Getter/Setter
}