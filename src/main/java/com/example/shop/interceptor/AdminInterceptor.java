package com.example.shop.interceptor;

import com.example.shop.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // 如果没登录，或者角色不是 ADMIN
        if (user == null || !"ADMIN".equals(user.getRole())) {
            response.sendRedirect("/login");
            return false; // 拦截下来，不让过
        }
        return true; // 放行
    }
}