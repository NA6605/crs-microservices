package vn.edu.crs.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthHeaderFilter implements GlobalFilter, Ordered {
    // Các đường dẫn KHÔNG cần Header Authorization
    private static final List<String> OPEN_PATHS = List.of(
            "/api/auth/login",
            "/api/public/courses"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. Luôn cho qua request OPTIONS (Preflight của trình duyệt để xử lý CORS)
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // 2. Cho qua các đường dẫn công khai đã khai báo
        boolean isOpen = OPEN_PATHS.stream().anyMatch(path::startsWith);

        // 3. GET /api/courses hoặc GET /api/courses/{id} là public (xem danh sách/chi tiết môn học không cần đăng nhập)
        boolean isPublicCourseRead = path.startsWith("/api/courses") && request.getMethod() == HttpMethod.GET;

        if (isOpen || isPublicCourseRead) {
            return chain.filter(exchange);
        }

        // 4. Các request còn lại (POST, PUT, DELETE hoặc /registrations/**) bắt buộc phải có Authorization
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || authHeader.isBlank()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; 
    }
}