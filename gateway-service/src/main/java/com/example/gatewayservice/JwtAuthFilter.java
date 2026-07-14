package com.example.gatewayservice;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    // 白名单：不需要 token 的路径
    private static final List<String> WHITELIST = List.of(
            "/api/auth/login",
            "/api/auth/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 第1步：获取请求路径
        String path = exchange.getRequest().getURI().getPath();

        // 第2步：白名单直接放行
        // 遍历 WHITELIST，如果 path 以某个白名单开头，return chain.filter(exchange)
        for (String whitelist : WHITELIST) {
            if (path.endsWith(whitelist)) {
                return chain.filter(exchange);
            }
        }

        // 第3步：从请求头取 Authorization
        // String auth = exchange.getRequest().getHeaders().getFirst("Authorization");
        String auth = exchange.getRequest().getHeaders().getFirst("Authorization");


        // 第4步：没有 token 或不是 Bearer 开头 → 返回 401
        // 用 exchange.getResponse() 设置状态码 401
        if (auth == null || !auth.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(401));
            return exchange.getResponse().setComplete();
        }

        // 第5步：提取 token，用 JwtUtil 校验
        // 校验失败 → 返回 401
        if (!JwtUtil.validateToken(auth.substring(7))) {
            exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(401));
            return exchange.getResponse().setComplete();
        }

        // 第6步：校验通过 → 放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;  // 优先级，数字越小越先执行
    }
}
