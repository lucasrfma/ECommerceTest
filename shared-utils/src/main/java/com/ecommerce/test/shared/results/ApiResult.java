package com.ecommerce.test.shared.results;

public sealed interface ApiResult<T> permits
        ApiResult.Success,
        ApiResult.ValidationFailure,
        ApiResult.Failure
{

    record Success<T>(T data) implements ApiResult<T> {}
    record ValidationFailure<T>(String message) implements ApiResult<T> {}
    record Failure<T>(String message) implements ApiResult<T> {}
}