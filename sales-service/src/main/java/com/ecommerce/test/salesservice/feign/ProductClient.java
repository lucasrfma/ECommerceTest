package com.ecommerce.test.salesservice.feign;

import com.ecommerce.test.shared.dtos.ProductDto;
import com.ecommerce.test.shared.dtos.UpsertProductDto;
import com.ecommerce.test.shared.results.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @PostMapping("/api/products")
    ResponseEntity<ApiResult<ProductDto>> RegisterProduct(@RequestBody UpsertProductDto upsertProductDto);

    @GetMapping("/api/products/{id}")
    ResponseEntity<ApiResult<ProductDto>> GetProductById(@PathVariable("id") Long id);
}
