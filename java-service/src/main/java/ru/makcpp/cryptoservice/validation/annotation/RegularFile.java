package ru.makcpp.cryptoservice.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.makcpp.cryptoservice.validation.RegularFileValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegularFileValidator.class)
public @interface RegularFile {
    String message() default "Путь не является обычным файлом";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
