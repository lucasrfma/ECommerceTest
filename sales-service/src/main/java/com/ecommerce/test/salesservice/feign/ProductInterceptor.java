package com.ecommerce.test.salesservice.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class ProductInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String auth_header = attributes.getRequest().getHeader("Authorization");
            if (auth_header != null) {
                template.header("Authorization", auth_header);
            }
        }
    }
}
