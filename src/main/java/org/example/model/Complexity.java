package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.example.model.error.UserError;

import static org.example.model.error.UserError.nonCompliantValue;

@Getter
@AllArgsConstructor
public enum Complexity {

    SIMPLE("Simple"),
    MODERATE("Moderate"),
    HAZARDOUS("Hazardous");

    private String string;

    @Override
    public String toString() {
        return this.getString();
    }

    public static Complexity fromString(@NonNull String caseInsensitiveValue) throws UserError {
        try {
            return Complexity.valueOf(caseInsensitiveValue.toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw nonCompliantValue(caseInsensitiveValue);
        }
    }
}