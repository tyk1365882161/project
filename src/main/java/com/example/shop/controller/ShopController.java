package com.example.shop.controller;

import com.example.shop.entity.*;
import com.example.shop.service.ShopService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ShopController {

    @Autowired private ShopService shopService;

    // 改造后的首页：既能看全部，也能看搜索结果
    @GetMapping("/")
    public String index(@RequestParam(required = false) String keyword, Model model) {
        List<Product> products;
        if (keyword != null && !keyword.isEmpty()) {
            // 如果有关键词，就搜
            products = shopService.searchProducts(keyword);
            model.addAttribute("keyword", keyword); // 把关键词传回去，回显在输入框里
        } else {
            // 没关键词，查所有
            products = shopService.getAllProducts();
        }
        model.addAttribute("products", products);
        return "index";
    }

    // 登录页面
    @GetMapping("/login")
    public String loginPage() { return "login"; }

    // 登录逻辑
    @PostMapping("/login")
    public String doLogin(String username, String password, HttpSession session, Model model) {
        User user = shopService.login(username, password);
        if (user == null) {
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }
        session.setAttribute("user", user);
        // 如果是管理员，去后台；否则去首页
        if ("ADMIN".equals(user.getRole())) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/";
    }

    // 加入购物车
    @GetMapping("/add-cart")
    public String addCart(@RequestParam Long pid, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        shopService.addToCart(user.getId(), pid, 1);
        return "redirect:/cart";
    }

    // 查看购物车
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("items", shopService.getCart(user.getId()));
        return "cart";
    }

    // 结账
    @PostMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        try {
            shopService.checkout(user.getId());
            model.addAttribute("msg", "下单成功！邮件已发送。");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "index"; // 返回首页并带提示
    }
    // --- 注册功能 ---

    // 1. 显示注册页面
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // 2. 处理注册请求
    @PostMapping("/register")
    public String doRegister(User user, Model model) {
        // user 对象会自动接收前端传来的 username, password, email
        try {
            shopService.register(user); // 调用之前 Service 写好的注册方法
            // 注册成功，跳回登录页，并带一个 success 参数用于显示提示
            return "redirect:/login?success"; 
        } catch (Exception e) {
            // 如果注册失败（比如用户名重复），留在注册页并显示错误
            model.addAttribute("error", "注册失败：用户名可能已存在");
            return "register";
        }
    }
    // --- 注销功能 ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 1. 销毁当前用户的 session (相当于清除登录状态)
        session.invalidate();
        // 2. 重定向回登录页面，并带个提示
        return "redirect:/login?logout";
    }
    // --- 处理删除请求 ---
    @GetMapping("/cart/delete")
    public String deleteCartItem(@RequestParam Long itemId, HttpSession session) {
        // 安全检查：没登录不能删
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // 调用 Service 删除
        shopService.removeCartItem(itemId);
        
        // 删完刷新购物车页面
        return "redirect:/cart";
    }
    // --- 我的订单页面 ---
    @GetMapping("/orders")
    public String orderHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // 获取该用户的所有订单
        List<ShopOrder> orders = shopService.getUserOrders(user.getId());
        model.addAttribute("orders", orders);
        
        return "orders"; // 返回 orders.html
    }
    // --- 商品详情页 (用于触发浏览日志) ---
    @GetMapping("/product/{id}") 
    public String productDetail(@PathVariable Long id, HttpSession session, Model model) 
    {   
        // 1. 查商品
        Product p = shopService.getProductById(id);
        
        // 如果 ID 不存在（比如用户乱输了个 id=9999），跳回首页
        if (p == null) {
            return "redirect:/";
        }
        
        // 2. 记日志 (如果用户已登录)
        User user = (User) session.getAttribute("user");
        if (user != null) {
            shopService.log(user, "BROWSE", "浏览了商品详情: " + p.getName());
        }

        // 3. 把商品数据传给 HTML 页面
        model.addAttribute("product", p);
        
        // 4. 返回我们刚才新建的 product_detail.html
        return "product_detail"; 
    }
}