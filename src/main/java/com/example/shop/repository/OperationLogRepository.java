package com.example.shop.repository;

import com.example.shop.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    
    // 查询所有日志，并且按照时间倒序排列（最新的在最上面）
    List<OperationLog> findAllByOrderByCreateTimeDesc();
    
}