package ru.makcpp.cryptoservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.makcpp.cryptoservice.validation.annotation.RegularFile;

import java.nio.file.Files;
import java.nio.file.Path;

public class RegularFileValidator implements ConstraintValidator<RegularFile, Path> {
    @Override
    public boolean isValid(Path value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return Files.isRegularFile(value);
    }
}
