package com.ecommerce.test.accountservice.results;

public sealed interface ApiResponse<T> permits
    ApiResponse.Success,
    ApiResponse.Failure {

    record Success<T>(T data) implements ApiResponse<T> {}
    record Failure<T>(String message) implements ApiResponse<T> {}
}
