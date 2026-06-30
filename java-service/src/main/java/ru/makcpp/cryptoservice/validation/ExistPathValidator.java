package ru.makcpp.cryptoservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.nio.file.Files;
import java.nio.file.Path;
import ru.makcpp.cryptoservice.validation.annotation.ExistPath;

public class ExistPathValidator implements ConstraintValidator<ExistPath, Path> {
    @Override
    public boolean isValid(Path value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return Files.exists(value);
    }
}
