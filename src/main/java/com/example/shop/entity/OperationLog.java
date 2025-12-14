package com.example.shop.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class OperationLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String username; // 冗余存一份，方便查询
    private String action;   // "BROWSE"(浏览), "BUY"(购买), "LOGIN"(登录)
    private String content;  // 详情，如 "浏览了 iPhone 15"
    private Date createTime = new Date();

    // 必须有的构造方法
    public OperationLog() {}
    public OperationLog(Long userId, String username, String action, String content) {
        this.userId = userId;
        this.username = username;
        this.action = action;
        this.content = content;
    }
    // 请务必生成 Getter/Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}