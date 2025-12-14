package com.example.shop.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class ShopOrder {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private String userEmail; // 冗余存一份，方便发邮件
    private Double totalPrice;
    private String status; // "待支付", "已支付", "已发货"
    private Date createTime = new Date();
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "shopOrder")
    private List<OrderItem> items; // 一个订单包含多个商品
    
    // 请生成 Getter/Setter
}