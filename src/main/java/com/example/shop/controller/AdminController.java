package com.example.shop.controller;

import com.example.shop.entity.*;
import com.example.shop.service.ShopService;

import java.util.Map;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin") // 所有路径都以 /admin 开头
public class AdminController {

    @Autowired private ShopService shopService;

    // 1. 后台首页 (销售统计报表)
    @GetMapping({"", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalSales", shopService.getTotalSales());
        model.addAttribute("orderCount", shopService.getAllOrders().size());
        model.addAttribute("productCount", shopService.getAllProducts().size());
        model.addAttribute("userCount", shopService.getAllUsers().size());

        // --- 新增：注入图表数据 ---
        Map<String, List<Object>> chartData = shopService.getSalesChartData();
        model.addAttribute("chartDates", chartData.get("dates"));   // X轴
        model.addAttribute("chartValues", chartData.get("values")); // Y轴
        return "admin/dashboard";
    }

    // 2. 商品管理：列表页
    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", shopService.getAllProducts());
        return "admin/products";
    }

    // 3. 商品管理：添加商品 (处理提交)
    @PostMapping("/products/add")
    public String addProduct(Product product) {
        // 简单的设个默认图，防止空的丑
        if (product.getImageUrl() == null || product.getImageUrl().isEmpty()) {
            product.setImageUrl("https://dummyimage.com/300x300/eee/aaa");
        }
        shopService.saveProduct(product);
        return "redirect:/admin/products";
    }

    // 4. 商品管理：删除
    @GetMapping("/products/delete")
    public String deleteProduct(@RequestParam Long id) {
        shopService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    // 5. 订单管理
    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", shopService.getAllOrders());
        return "admin/orders";
    }

    // 6. 订单发货
    @GetMapping("/orders/ship")
    public String shipOrder(@RequestParam Long id) {
        shopService.shipOrder(id);
        return "redirect:/admin/orders";
    }
    
    // 7. 客户管理 & 日志
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", shopService.getAllUsers());
        model.addAttribute("logs", shopService.getAllLogs()); // 把日志也传过去
        return "admin/users";
    }
    // --- 进入商品编辑页面 ---
    @GetMapping("/products/edit")
    public String editProductPage(@RequestParam Long id, Model model) {
        Product p = shopService.getProductById(id);
        model.addAttribute("product", p); // 把旧数据传给前端回显
        return "admin/product_edit"; // 我们需要新建这个页面
    }

    // --- 执行商品更新 ---
    @PostMapping("/products/update")
    public String updateProduct(Product product) {
        // product 对象里会包含 id, name, price 等所有字段
        shopService.saveProduct(product);
        return "redirect:/admin/products";
    }
    
    // --- 删除订单接口 ---
    @GetMapping("/orders/delete")
    public String deleteOrder(@RequestParam Long id) {
        shopService.deleteOrder(id);
        return "redirect:/admin/orders";
    }

    // --- 删除日志接口 ---
    @GetMapping("/logs/delete") // 注意这里路径别跟其他的冲突
    public String deleteLog(@RequestParam Long id) {
        shopService.deleteLog(id);
        return "redirect:/admin/users"; // 删完回用户/日志页
    }
}