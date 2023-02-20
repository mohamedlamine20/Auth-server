package com.personel.auth.server.validators;

import com.personel.auth.server.exceptions.ResourceAlreadyExistsException;
import com.personel.auth.server.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailConstraintValidator implements ConstraintValidator<ValidEmail,String> {
    @Autowired
    UserRepository userRepository;
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (!userRepository.existsByEmail(email)) {
            return true;
        }
        context.buildConstraintViolationWithTemplate("Error: Email is already in use!")
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }
}
