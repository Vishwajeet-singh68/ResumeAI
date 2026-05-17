package org.app.resumeaigateway.filter;

import org.app.resumeaigateway.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public AuthFilter() {
        super(Config.class);
    }

    public static class Config {
    }

    // ✅ Public endpoints (no auth required)
    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html"
    );

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();

            // ✅ Skip public endpoints
            if (isPublic(path)) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            // ❌ No header or wrong format
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                // ✅ Validate token
                if (!jwtUtil.validateToken(token)) {
                    return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
                }

                String username = jwtUtil.extractUsername(token);
                String userId = jwtUtil.extractUserId(token);
                String role = jwtUtil.extractRole(token);
                String subscriptionPlan = jwtUtil.extractSubscriptionPlan(token);

                // ✅ Pass user info downstream for RBAC
                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("X-User-Name", username != null ? username : "")
                                .header("X-User-Id", userId != null ? userId : "")
                                .header("X-User-Role", role != null ? role : "")
                                .header("X-Subscription", subscriptionPlan != null ? subscriptionPlan : "")
                                .build())
                        .build();

                return chain.filter(mutatedExchange);

            } catch (Exception e) {
                return onError(exchange, "Token validation failed", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    // ✅ Check if path is public
    private boolean isPublic(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::contains);
    }

    private reactor.core.publisher.Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
}