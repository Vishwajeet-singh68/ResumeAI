package org.app.resumeaigateway.filter;

import org.app.resumeaigateway.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public AuthFilter() {
        super(Config.class);
    }

    public static class Config {
        // optional configs
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            // ❌ No header or wrong format
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            // ✅ Extract token
            String token = authHeader.substring(7);

            try {
                // ✅ Validate token

                if (!jwtUtil.validateToken(token)) {
                    return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
                }


                // ✅ Extract user details (optional but recommended 🔥)
                String username = jwtUtil.extractUsername(token);
//                String role = jwtUtil.extractRole(token); // if you store role in JWT

                // ✅ Add headers to downstream services
                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("X-User-Name", username)
//                                .header("X-User-Role", role != null ? role : "")
                                .build())
                        .build();

                // ✅ Continue filter chain
                return chain.filter(mutatedExchange);

            } catch (Exception e) {
                return onError(exchange, "Token validation failed", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    // 🔥 Common error handler
    private reactor.core.publisher.Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
}