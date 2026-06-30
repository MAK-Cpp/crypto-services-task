package ru.makcpp.cryptoservice.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ru.makcpp.cryptoservice.validation.ExistPathValidator;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistPathValidator.class)
public @interface ExistPath {
    String message() default "Путь не существует";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
