package com.ecommerce.test.accountservice.results;

public sealed interface RegistrationResult permits
        RegistrationResult.Success,
        RegistrationResult.EmailAlreadyExists,
        RegistrationResult.ValidationError,
        RegistrationResult.UnknownError
{

    record Success() implements RegistrationResult {}
    record EmailAlreadyExists() implements RegistrationResult {}
    record ValidationError(String message) implements RegistrationResult {}
    record UnknownError() implements RegistrationResult {}
}

