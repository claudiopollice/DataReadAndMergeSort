package org.example.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.example.model.error.UserError;
import org.example.util.LocalDateTimeParser;

import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.example.TestUtil.*;


class DataEntryTest {

    @Test
    @DisplayName("should transform and store input values and store startdate for sorting")
    void should_transform_and_store_input_values_and_store_startdate_for_sorting() throws UserError {
        DataEntry dataEntry = dataEntry(LINE_WITH_NULL_VALUES);
        assertEquals("2013-01-01 00:00:00.000", LocalDateTimeParser.toString(dataEntry.getStartDate()));
        assertEquals(List.of("val1", "val2", "2013-01-01 00:00:00.000", "val4", "val5", " ", " ", "Simple"), dataEntry.getEntryOutputValues());
    }

    @Test
    @DisplayName("should throw error for wrong date time format")
    void should_throw_error_for_wrong_date_time_format() throws UserError {
        Throwable thrown = catchThrowable(() -> dataEntry(LINE_WITH_WRONG_DATE_FORMAT));
        assertTrue(thrown.getMessage().contains("Start date has to be of format yyyy-MM-dd HH:mm:ss.SSS."));
        assertTrue(thrown.getMessage().contains("Error occured in line: 0."));
    }

    @Test
    @DisplayName("should throw error for not allowed complexity value")
    void should_throw_error_for_not_allowed_complexity_value() {
        Throwable thrown = catchThrowable(() -> dataEntry(LINE_WITH_NOT_ALLOWED_COMPLEXITY));
        assertTrue(thrown.getMessage().contains("Non-compliant value: Very High."));
        assertTrue(thrown.getMessage().contains("Error occured in line: 0."));
    }

    @Test
    @DisplayName("should throw error for savings amount with wrong format")
    void should_throw_error_for_savings_amount_with_wrong_format() {
        Throwable thrown = catchThrowable(() -> dataEntry(LINE_WITH_WRONG_MONEY_FORMAT));
        assertTrue(thrown.getMessage().contains("Savings amount values can only contain numbers and one decimal point."));
        assertTrue(thrown.getMessage().contains("Error occured in line: 0."));
    }

    @Test
    @DisplayName("should throw error for empty value")
    void should_throw_error_for_empty_value() {
        Throwable thrown = catchThrowable(() -> dataEntry(LINE_WITH_EMPTY_VALUES));
        assertTrue(thrown.getMessage().contains("Empty values are not allowed."));
        assertTrue(thrown.getMessage().contains("Error occured in line: 0."));
    }

    @Test
    @DisplayName("should return -1 for earlier date first and 1 for later date first")
    void should_return_true_for_earlier_date_first_and_false_for_later_date_first() throws UserError {
        DataEntry earlierDataEntry = dataEntry(LINE_2_2013);
        DataEntry laterDataEntry = dataEntry(LINE_1_2014);

        assertEquals(-1, earlierDataEntry.compareTo(laterDataEntry));
        assertEquals(1, laterDataEntry.compareTo(earlierDataEntry));
    }
}