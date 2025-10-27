package com.ecommerce.test.shared.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ecommerce.test.shared.exceptions.ManualValidationException;

public class MiscUtils {
    public record Principal(Long id, String email){}

    public static Principal getCurrentPrincipal()
            throws ManualValidationException, NumberFormatException, ArrayIndexOutOfBoundsException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new ManualValidationException("Erro ao obter informação de usuário.");
        }

        String principal = (String) auth.getPrincipal();
        String[] parts = principal.split("!");
        Long accId = Long.parseLong(parts[0]);
        String email = parts[1];
        return new Principal(accId, email);
    }
}
