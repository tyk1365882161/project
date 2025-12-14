package com.example.shop.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    private String password;
    private String email;
    private String role; // "ADMIN" 或 "CUSTOMER"
    private Date createTime = new Date();

    // 必须有的无参构造
    public User() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    } 
    
    // 省略 Getter/Setter，请务必生成！(VS Code: 右键 -> Source Action -> Generate Getters and Setters)
    // 为了省空间，我下面就不写Getter/Setter了，你自己生成一下
}