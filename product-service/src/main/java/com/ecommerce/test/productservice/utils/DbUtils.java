package com.ecommerce.test.productservice.utils;

import com.ecommerce.test.productservice.results.ApiResult;
import jakarta.validation.ConstraintViolationException;

import java.util.function.Function;

public class DbUtils {

    /***
     * Função de alta ordem que serve para criar uma nova função mais protegida a partir de uma função
     * que realiza alguma inserção ou atualização de valores no banco de dados.
     * @param function função que insere ou atualiza valores no banco de dados.
     * @return nova função cujo retorno é o da função interna envolvido em um ApiResult.
     * Essa esse retorno é Success em casos que a função original funcionaria corretamente, e Failure em ocasiões
     * em que a função original lançaria uma exceção.
     * @param <T> Um tipo qualquer, mas provavelmente uma entidade do banco de dados.
     * @param <U> Um tipo qualquer, mas provavelmente um DTO baseado da entidade do banco de dados.
     */
    public static <T, U> Function<T, ApiResult<U>> ProtectDbFunction(Function<T, U> function) {
        return (input) -> {
            try {
                return new ApiResult.Success<>(function.apply(input));
            } catch (ConstraintViolationException e) {
                String violations = e.getConstraintViolations().stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .reduce((a, b) -> a + "; " + b)
                        .orElse("Erro de validação");
                return new ApiResult.Failure<>(violations);
            } catch (Exception e) {
                return new ApiResult.Failure<>("Erro interno inesperado");
            }
        };
    }

}
