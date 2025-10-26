package com.ecommerce.test.gatewayservice.configurations;

import com.ecommerce.test.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@Slf4j
public class JwtAuthenticationFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(@Value("${jwt.secret}") String jwtSecret, @Value("${jwt.duration}") Duration duration) {
        jwtUtil = new JwtUtil(jwtSecret, duration);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!isOpenEndpoint(exchange)) {
            log.info("Verificando Authorization");
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorizedResponse(exchange, "Authorization header faltando ou inválido");
            }

            String jwt = authHeader.substring(7);
            try {
                // na hora de parsear o token, já é visto que está expirado.
                // essa função isTokenExpired perde o sentido
                // todo: remover
                if (jwtUtil.isTokenExpired(jwt)) {
                    return unauthorizedResponse(exchange, "JWT token expirado");
                }
            }
            catch (ExpiredJwtException e) {
                return unauthorizedResponse(exchange, "JWT token expirado");
            }
            catch (Exception e) {
                log.error("Error validating JWT token", e);
                return unauthorizedResponse(exchange, "JWT token inválido");
            }
        }
        log.info("Routing request to specific service");
        return chain.filter(exchange);
    }

    private boolean isOpenEndpoint(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        if (method == HttpMethod.GET) {
            return path.equals("/api/products");
        }
        else if (method == HttpMethod.POST) {
            // Para seguir uma lógica de segurança em primeiro lugar, defino as regras para pular autenticação
            // especificamente para cada endpoint que deve ser de livre acesso.
            // (usando dois equals, em vez do "startsWith",
            //  mesmo com todos os endpoints de accounts sendo livres atualmente.)
            return path.equals("/api/accounts/register") || path.equals("/api/accounts/login");
        }
        return false;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(message.getBytes())));
    }
}
