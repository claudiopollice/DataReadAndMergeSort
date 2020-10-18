package org.example.process;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.example.model.error.UserError;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.example.TestUtil.*;
import static org.example.model.DataColumn.DESCRIPTION;
import static org.example.model.DataColumn.PROJECT;

class LineProcessorTest {

    @Test
    @DisplayName("should only increase index for commented and blank lines")
    void should_only_increase_index_for_commented_lines() throws UserError {
        LineProcessor lineProcessor = newLineProcesser(false, false, null);
        lineProcessor.setHeaderIsProcessed(true);

        String[] lines = {COMMENTED_LINE, EMPTY_LINE, WHITE_SPACE_LINE, COMMENTED_LINE, LINE_2_2013};
        for (String line : lines) {
            lineProcessor.basicProcessing(line).storeInMemory();
        }

        assertAll("validate memory state unchanged",
                () -> assertEquals(1, lineProcessor.getContext().getStateInMemory().getDataEntries().size()),
                () -> assertEquals(5, lineProcessor.getLineIndex())
        );
    }

    @Test
    @DisplayName("should process first non commented line as header and set header as processed")
    void should_process_first_non_commented_line_as_header_and_set_header_as_processed() throws UserError {
        LineProcessor lineProcessor = newLineProcesser(false, false, null);
        assertFalse(lineProcessor.isHeaderIsProcessed());
        lineProcessor.basicProcessing(HEADER_LINE);
        assertTrue(lineProcessor.isHeaderIsProcessed());
    }

    @Test
    @DisplayName("should not depend on column order as long as header and data entries are consistent")
    void should_not_depend_on_column_order_as_long_as_header_and_data_entries_are_consistent() throws UserError {
        LineProcessor lineProcessor = newLineProcesser(false, false, null);

        assertFalse(lineProcessor.isHeaderIsProcessed());
        lineProcessor.basicProcessing(HEADER_LINE);
        assertTrue(lineProcessor.isHeaderIsProcessed());
        assertEquals(PROJECT.getString(), lineProcessor.getContext().getColumnOrderMap().get(0));
        assertNotNull(catchThrowable(() -> lineProcessor.basicProcessing(LINE_WITH_VALUES_DIFFERENT_ORDER).storeInMemory()));
        assertNull(catchThrowable(() -> lineProcessor.basicProcessing(LINE_2_2013).storeInMemory()));

        lineProcessor.setHeaderIsProcessed(false);
        assertFalse(lineProcessor.isHeaderIsProcessed());
        lineProcessor.basicProcessing(HEADER_LINE_DIFFERENT_ORDER);
        assertTrue(lineProcessor.isHeaderIsProcessed());
        assertEquals(DESCRIPTION.getString(), lineProcessor.getContext().getColumnOrderMap().get(0));
        assertNotNull(catchThrowable(() -> lineProcessor.basicProcessing(LINE_2_2013).storeInMemory()));
        assertNull(catchThrowable(() -> lineProcessor.basicProcessing(LINE_WITH_VALUES_DIFFERENT_ORDER).storeInMemory()));
    }

    @Test
    @DisplayName("should throw error if first non commented line is not a valid header line")
    void should_throw_error_if_first_non_commented_line_is_not_a_valid_header_line() throws UserError {
        LineProcessor lineProcessor = newLineProcesser(false, false, null);
        lineProcessor.setHeaderIsProcessed(false);
        Throwable thrown = catchThrowable(() -> lineProcessor.basicProcessing(LINE_2_2013));
        assertEquals(thrown.getMessage(), UserError.corruptHeaderLineError(LINE_2_2013, "3", 1).getMessage());
    }

    @Test
    @DisplayName("should successfully store line in memory if that is the strategy")
    void should_successfully_store_line_in_memory_if_that_is_the_strategy() throws UserError {
        LineProcessor lineProcessor = newLineProcesser(false, false, null);
        lineProcessor.setHeaderIsProcessed(true);

        String[] lines = {LINE_2_2013, LINE_2_2013, LINE_2_2013, LINE_2_2013, LINE_2_2013};
        for (String line : lines) {
            lineProcessor.basicProcessing(line).storeInMemory();
        }

        assertAll("validate memory state change",
                () -> assertEquals(5, lineProcessor.getContext().getStateInMemory().getDataEntries().size()),
                () -> assertEquals(5, lineProcessor.getLineIndex())
        );
    }

    @Test
    @DisplayName("should mark line as processed when printing immediately")
    void should_mark_line_as_processed_when_printing_immediately() throws UserError {
        LineProcessor lineProcessor = newLineProcesser(false, false, null);
        lineProcessor.setHeaderIsProcessed(true);
        assertFalse(lineProcessor.isCurrentLineIsProcessed());
        lineProcessor.basicProcessing(LINE_2_2013).printLineImmediately();
        assertTrue(lineProcessor.isCurrentLineIsProcessed());
    }

    @Test
    @DisplayName("should mark line as processed and not add to memory if does not matches project filter")
    void should_mark_line_as_processed_and_not_add_to_memory_if_matches_project_filter() throws UserError {
        LineProcessor lineProcessor = newLineProcesser(false, true, "val2");
        lineProcessor.setHeaderIsProcessed(true);

        lineProcessor.basicProcessing(LINE_2_2013).storeInMemory();
        assertTrue(lineProcessor.isCurrentLineIsProcessed());
        assertEquals(0, lineProcessor.getContext().getStateInMemory().getDataEntries().size());
    }

    @Test
    @DisplayName("should throw error if date has wrong format specifying the line and index")
    void should_throw_error_if_date_has_wrong_format_specifying_the_line_and_index() throws UserError {
        LineProcessor lineProcessor = newLineProcesser(false, false, null);
        lineProcessor.setHeaderIsProcessed(true);

        Throwable thrown = catchThrowable(() -> lineProcessor.basicProcessing(LINE_WITH_WRONG_DATE_FORMAT));

        assertAll("validate error specificity",
                () -> assertTrue(thrown.getMessage().contains("Start date has to be of format yyyy-MM-dd HH:mm:ss.SSS.")),
                () -> assertTrue(thrown.getMessage().contains("Non-compliant value: " + INVALID_DATE)),
                () -> assertTrue(thrown.getMessage().contains("Line content: " + LINE_WITH_WRONG_DATE_FORMAT)),
                () -> assertTrue(thrown.getMessage().contains("Error occured in line: 1"))
        );
    }

    @Test
    @DisplayName("should throw error if savings amount has wrong format specifying the line and index")
    void should_throw_error_if_savings_amount_has_wrong_format_specifying_the_line_and_index() throws UserError {
        LineProcessor lineProcessor = newLineProcesser(false, false, null);
        lineProcessor.setHeaderIsProcessed(true);

        Throwable thrown = catchThrowable(() -> lineProcessor.basicProcessing(LINE_WITH_WRONG_MONEY_FORMAT));

        assertAll("validate error specificity",
                () -> assertTrue(thrown.getMessage().contains("Savings amount values can only contain numbers and one decimal point.")),
                () -> assertTrue(thrown.getMessage().contains("Non-compliant value: " + INVALID_SAVINGS_AMOUNT)),
                () -> assertTrue(thrown.getMessage().contains("Error occured in line: 1"))
        );
    }

    @Test
    @DisplayName("should throw error if complexity has not allowed value specifying the line and index")
    void should_throw_error_if_complexity_has_not_allowed_value_specifying_the_line_and_index() throws UserError {
        LineProcessor lineProcessor = newLineProcesser(false, false, null);
        lineProcessor.setHeaderIsProcessed(true);

        Throwable thrown = catchThrowable(() -> lineProcessor.basicProcessing(LINE_WITH_NOT_ALLOWED_COMPLEXITY));

        assertAll("validate error specificity",
                () -> assertTrue(thrown.getMessage().contains("Non-compliant value: " + NOT_ALLOWED_COMPLEXITY)),
                () -> assertTrue(thrown.getMessage().contains("Error occured in line: 1"))
        );
    }

    @Test
    @DisplayName("should throw error if line has wrong number of columns")
    void should_throw_error_if_line_has_wrong_number_of_columns() throws UserError {
        LineProcessor lineProcessor = newLineProcesser(false, false, null);
        lineProcessor.setHeaderIsProcessed(true);

        Throwable thrown1 = catchThrowable(() -> lineProcessor.basicProcessing(LINE_WITH_7_VALUES));
        Throwable thrown2 = catchThrowable(() -> lineProcessor.basicProcessing(LINE_WITH_9_VALUES));

        assertAll("validate error specificity",
                () -> assertTrue(thrown1.getMessage().contains("Expected to find 8 values in each line, but instead found 7 values")),
                () -> assertTrue(thrown1.getMessage().contains("Line content: " + LINE_WITH_7_VALUES)),
                () -> assertTrue(thrown1.getMessage().contains("Error occured in line: 1")),
                () -> assertTrue(thrown2.getMessage().contains("Expected to find 8 values in each line, but instead found 9 values")),
                () -> assertTrue(thrown2.getMessage().contains("Line content: " + LINE_WITH_9_VALUES)),
                () -> assertTrue(thrown2.getMessage().contains("Error occured in line: 2"))
        );
    }

    @Test
    @DisplayName("should replace NULL with empty string for savings amount and currency ")
    void should_replace_NULL_with_empty_string_for_savings_amount_and_currency() throws UserError {
        LineProcessor lineProcessor = newLineProcesser(false, false, null);
        lineProcessor.setHeaderIsProcessed(true);
        lineProcessor.basicProcessing(LINE_WITH_NULL_VALUES).storeInMemory();
        String output = String.join(TAB, lineProcessor.getContext().getStateInMemory().getDataEntries().get(0).getEntryOutputValues());
        assertEquals(LINE_WITH_NULL_VALUES_REPLACED, output);
    }

    @Test
    @DisplayName("should throw error for empty value in data entry")
    void should_throw_error_for_empty_value_in_data_entry() throws UserError {
        LineProcessor lineProcessor = newLineProcesser(false, false, null);
        lineProcessor.setHeaderIsProcessed(true);
        Throwable thrown = catchThrowable(() -> lineProcessor.basicProcessing(LINE_WITH_EMPTY_VALUES).storeInMemory());
        assertTrue(thrown.getMessage().contains("Empty values are not allowed."));
        assertTrue(thrown.getMessage().contains("Error occured in line: 1."));
    }

}