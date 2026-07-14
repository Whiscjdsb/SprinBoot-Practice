package com.example.springbootpractice.aspect;

import com.example.springbootpractice.annotation.RequiresPermission;
import com.example.springbootpractice.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

    @Around("@annotation(requiresPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequiresPermission requiresPermission) throws Throwable {
        // 拿到当前登录用户
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 检查是否有"ROLE_admin"
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_admin"));

        if (!isAdmin) {
            throw new BusinessException(403, "没有权限，仅管理员可操作");
        }

        return joinPoint.proceed();
    }
}
