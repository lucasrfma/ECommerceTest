package com.ecommerce.test.shared.results;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ApiResult.Success.class, name = "success"),
        @JsonSubTypes.Type(value = ApiResult.ValidationFailure.class, name = "val_failure"),
        @JsonSubTypes.Type(value = ApiResult.Failure.class, name = "failure")
})
public sealed interface ApiResult<T> permits
        ApiResult.Success,
        ApiResult.ValidationFailure,
        ApiResult.Failure
{
    @JsonIgnore
    default T getSuccessData() {
        if (isSuccess()) {
            return ((Success<T>) this).data;
        }
        throw new IllegalStateException("Result is not a success.");
    }
    @JsonIgnore
    default boolean isSuccess() {
        return this instanceof ApiResult.Success;
    }

    @JsonIgnore
    default String getErrorMessage() {
        if (this instanceof ApiResult.ValidationFailure) {
            return ((ValidationFailure<T>) this).message;
        }
        if (this instanceof ApiResult.Failure) {
            return ((Failure<T>) this).message;
        }
        return "";
    }

    record Success<T>(T data) implements ApiResult<T> {}
    record ValidationFailure<T>(String message) implements ApiResult<T> {}
    record Failure<T>(String message) implements ApiResult<T> {}
}