package com.personel.auth.server.validators;

import com.personel.auth.server.exceptions.ObjectNotValidException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ObjectsValidator<T> {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    public void validate(T object){

        Set<ConstraintViolation<T>> validations = validator.validate(object);

        if(!validations.isEmpty()){
            throw new ObjectNotValidException(validations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toSet()).toString());
        }
    }
}
