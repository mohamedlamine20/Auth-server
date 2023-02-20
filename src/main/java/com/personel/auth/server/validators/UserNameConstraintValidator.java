package com.personel.auth.server.validators;

import com.personel.auth.server.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UserNameConstraintValidator implements ConstraintValidator<ValidUserName,String> {
    @Autowired
    UserRepository userRepository;

    @Override
    public void initialize(ValidUserName constraintAnnotation) {
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (!userRepository.existsByUsername(username)) {
           return true;
       }
        context.buildConstraintViolationWithTemplate("Error: Username is already taken!")
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }
}
