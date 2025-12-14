package com.example.shop.service;

import com.example.shop.entity.*;
import com.example.shop.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ShopService {

    @Autowired private UserRepository userRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private CartItemRepository cartRepo;
    @Autowired private ShopOrderRepository orderRepo;
    @Autowired private JavaMailSender mailSender; // 邮件发送器

    // --- 用户功能 ---
    public User login(String username, String password) {
        User user = userRepo.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public void register(User user) {
        user.setRole("CUSTOMER"); // 默认注册为普通用户
        userRepo.save(user);
    }

    // --- 购物车功能 ---
    public void addToCart(Long userId, Long productId, int quantity) {
        Product p = productRepo.findById(productId).get();
        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProduct(p);
        item.setQuantity(quantity);
        cartRepo.save(item);
    }

    public List<CartItem> getCart(Long userId) {
        return cartRepo.findByUserId(userId);
    }

    // --- 下单功能 (核心事务) ---
    @Transactional // 保证原子性
    public void checkout(Long userId) {
        List<CartItem> cartItems = cartRepo.findByUserId(userId);
        if (cartItems.isEmpty()) throw new RuntimeException("购物车为空");

        User user = userRepo.findById(userId).get();
        double total = 0;
        List<OrderItem> orderItems = new ArrayList<>();
        ShopOrder order = new ShopOrder();

        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            // 1. 扣库存
            if (p.getStock() < ci.getQuantity()) throw new RuntimeException(p.getName() + " 库存不足");
            p.setStock(p.getStock() - ci.getQuantity());
            productRepo.save(p);

            // 2. 累加金额
            total += p.getPrice() * ci.getQuantity();

            // 3. 生成订单项
            OrderItem oi = new OrderItem();
            oi.setProductName(p.getName());
            oi.setPrice(p.getPrice());
            oi.setQuantity(ci.getQuantity());
            oi.setShopOrder(order); // 关联订单
            orderItems.add(oi);
        }

        // 4. 保存订单
        order.setUserId(userId);
        order.setUserEmail(user.getEmail());
        order.setTotalPrice(total);
        order.setStatus("已支付");
        order.setItems(orderItems);
        orderRepo.save(order);

        // 5. 清空购物车
        cartRepo.deleteByUserId(userId);

        // 6. 发送邮件 (简单实现，真正开发建议异步)
        sendEmail(user.getEmail(), "订单确认", "您的订单已创建，总金额：$" + total);

        log(user, "BUY", "购买了商品，订单金额: " + total);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("1365882161@qq.com"); // 记得去 application.properties 配置
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("邮件发送失败：" + e.getMessage());
        }
    }
    
    // --- 商品管理 (Admin用) ---
    public List<Product> getAllProducts() { return productRepo.findAll(); }
    public void saveProduct(Product p) { productRepo.save(p); }

    // --- 删除购物车商品 ---
    public void removeCartItem(Long cartItemId) {
        cartRepo.deleteById(cartItemId);
    }
    // --- 搜索商品 ---
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepo.findAll();
        }
        return productRepo.findByNameContainingIgnoreCase(keyword);
    }
    // --- 获取用户的所有订单 ---
    public List<ShopOrder> getUserOrders(Long userId) {
        // 按照创建时间倒序排列 (最新的在前面)
        return orderRepo.findByUserIdOrderByCreateTimeDesc(userId); 
    }

    @Autowired private OperationLogRepository logRepo;

    // --- 日志记录功能 ---
    public void log(User user, String action, String content) {
        if (user != null) {
            logRepo.save(new OperationLog(user.getId(), user.getUsername(), action, content));
        }
    }

    public List<OperationLog> getAllLogs() {
        return logRepo.findAllByOrderByCreateTimeDesc();
    }

    // --- 后台管理功能 ---
    
    // 1. 删除商品
    public void deleteProduct(Long id) {
        productRepo.deleteById(id);
    }

    // 2. 发货
    public void shipOrder(Long orderId) {
        ShopOrder order = orderRepo.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus("已发货");
            orderRepo.save(order);
        }
    }
    
    // 3. 获取所有订单
    public List<ShopOrder> getAllOrders() {
        return orderRepo.findAll(); // 实际项目建议分页
    }

    // 4. 获取所有用户
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // 5. 统计总销售额 (用于报表)
    public Double getTotalSales() {
        // 这里简单粗暴，把所有已支付订单加起来
        return orderRepo.findAll().stream()
                .mapToDouble(ShopOrder::getTotalPrice)
                .sum();
    }

    // --- 获取图表数据 (X轴日期, Y轴金额) ---
    public Map<String, List<Object>> getSalesChartData() {
        // 1. 获取数据库查出来的原始数据 (可能是稀疏的，比如只有周一和周三有数据)
        List<Object[]> rawData = orderRepo.findLast7DaysSales();
        
        // 把原始数据转成 Map 方便查找: {"2023-12-10": 100.0, "2023-12-12": 50.0}
        Map<String, Double> salesMap = new HashMap<>();
        for (Object[] row : rawData) {
            String date = (String) row[0];
            Double total = (Double) row[1];
            salesMap.put(date, total);
        }

        // 2. 准备连续的最近 7 天日期，防止断层
        List<Object> dateList = new ArrayList<>(); // X轴
        List<Object> valueList = new ArrayList<>(); // Y轴
        
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 循环过去 7 天 (从6天前到今天)
        for (int i = 6; i >= 0; i--) {
            String dateStr = LocalDate.now().minusDays(i).format(fmt);
            dateList.add(dateStr); // 加入X轴
            
            // 如果这一天有数据，就填数据；没有就填 0
            valueList.add(salesMap.getOrDefault(dateStr, 0.0));
        }

        // 3. 打包返回
        Map<String, List<Object>> result = new HashMap<>();
        result.put("dates", dateList);
        result.put("values", valueList);
        return result;
    }
    // --- 根据ID获取单个商品 ---
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElse(null);
    }
    // --- 删除订单 ---
    public void deleteOrder(Long id) {
        orderRepo.deleteById(id);
    }

    // --- 删除日志 ---
    public void deleteLog(Long id) {
        logRepo.deleteById(id);
    }
}