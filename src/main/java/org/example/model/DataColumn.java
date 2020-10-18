package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum DataColumn {

    PROJECT("Project"),
    DESCRIPTION("Description"),
    START_DATE("Start date"),
    CATEGORY("Category"),
    RESPONSIBLE("Responsible"),
    SAVINGS_AMOUNT("Savings amount"),
    CURRENCY("Currency"),
    COMPLEXITY("Complexity");

    private static final String STRING_DELIM = " ";
    private static final String ENUM_DELIM = "_";

    private String string;

    @Override
    public String toString() {
        return this.getString();
    }

    public static DataColumn fromString(@NonNull String caseInsensitiveValue) {
        return DataColumn.valueOf(caseInsensitiveValue.replace(STRING_DELIM, ENUM_DELIM).toUpperCase());
    }
}
