package com.ecommerce.test.productservice.results;

public sealed interface ApiResult<T> permits
        ApiResult.Success,
        ApiResult.Failure {

    record Success<T>(T data) implements ApiResult<T> {}
    record Failure<T>(String message) implements ApiResult<T> {}
}

