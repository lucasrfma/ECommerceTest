package com.ecommerce.test.gatewayservice.configurations;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class SimpleGatewayFilterFactory extends AbstractGatewayFilterFactory<SimpleGatewayFilterFactory.Config> {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    SimpleGatewayFilterFactory(JwtAuthenticationFilter jwtAuthenticationFilter) {
        super(Config.class);
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return jwtAuthenticationFilter;
    }

    static class Config {    }
}
