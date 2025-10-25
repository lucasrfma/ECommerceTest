package com.ecommerce.test.productservice.results;

import com.ecommerce.test.productservice.dtos.ProductDto;

public sealed interface RegistrationResult permits
        RegistrationResult.Registered,
        RegistrationResult.Updated,
        RegistrationResult.ValidationError,
        RegistrationResult.UnknownError
{

    record Registered() implements RegistrationResult {}
    record Updated(ProductDto updatedProdDto) implements RegistrationResult {}
    record ValidationError(String message) implements RegistrationResult {}
    record UnknownError() implements RegistrationResult {}
}
