package org.example.process;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.example.model.error.UserError;

import java.util.Collections;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.example.TestUtil.*;

class ContextTest {

    @Test
    @DisplayName("should throw error if header line does not contain all columns")
    void should_throw_error_if_header_line_does_not_contain_all_columns() throws UserError {
        Context context = context();
        context.populateColumnOrderMap(HEADER_LINE_MISSING_COMPLEXITY, 1);
        Throwable thrown = catchThrowable(context::allColumnsPresent);
        assertEquals(thrown.getMessage(), UserError.columnValidationError(Collections.singletonList("Complexity")).getMessage());
    }

    @Test
    @DisplayName("should throw error if header line contains illegal value")
    void should_throw_error_if_header_line_contains_illegal_value() throws UserError {
        Context context = context();
        Throwable thrown = catchThrowable(() -> context.populateColumnOrderMap(HEADER_LINE_WITH_ILLEGAL_VALUE, 1));
        assertEquals(thrown.getMessage(), UserError.corruptHeaderLineError(HEADER_LINE_WITH_ILLEGAL_VALUE, "Complexityy", 1).getMessage());
    }

    @Test
    @DisplayName("should create proper context for proper header line")
    void should_create_proper_context_for_proper_header_line() throws UserError {
        Context context = context();
        context.populateColumnOrderMap(HEADER_LINE, 1);
        context.allColumnsPresent();
        assertAll("validating context",
                () -> assertEquals("smalldatafile.txt", context.getInputFile().getName()),
                () -> assertNotNull(context.getStateInMemory()),
                () -> assertEquals(8, context.getColumnsSet().size()),
                () -> assertEquals(0, (int) context.getProjectColumn()),
                () -> assertEquals(2, (int) context.getDateColumn())
        );
    }
}