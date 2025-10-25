package com.ecommerce.test.accountservice.results;

public sealed interface LoginResult permits
        LoginResult.Success,
        LoginResult.Failure {

    record Success(String access_token) implements LoginResult {}
    record Failure(String error_message) implements LoginResult {}
}
