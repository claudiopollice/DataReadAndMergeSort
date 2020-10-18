package org.example.process;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.example.model.error.UserError;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.example.TestUtil.WHITE_SPACE_REGEX;

class CommandLineOptionsTest {

    @Test
    @DisplayName("should throw error if file isn't supploed")
    void should_throw_error_if_file_isnt_supplied() {
        String[] args = "".split(WHITE_SPACE_REGEX);
        Throwable thrown = catchThrowable(() -> new CommandLineOptions().processCommandlineOptions(args).extractFile());
        assertThat(thrown.getMessage()).isEqualTo(UserError.invalidOptionsError().getMessage());
    }

    @Test
    @DisplayName("should throw error if file doesn't exist")
    void should_throw_error_if_file_doesnt_exist() {
        String[] args = "-f absentFile".split(WHITE_SPACE_REGEX);
        Throwable thrown = catchThrowable(() -> new CommandLineOptions().processCommandlineOptions(args).extractFile());
        assertThat(thrown.getMessage()).isEqualTo(UserError.missingFileError().getMessage());
    }

    @Test
    @DisplayName("should handle file commandline option if file is supplied and exists")
    void should_handle_file_commandline_option_or_throw_error() throws UserError {
        String[] args = "-f absentFile".split(WHITE_SPACE_REGEX);
        Throwable thrown = catchThrowable(() -> new CommandLineOptions().processCommandlineOptions(args).extractFile());
        assertThat(thrown.getMessage()).isEqualTo(UserError.missingFileError().getMessage());

        String[] args1 = "-f smalldatafile.txt".split(WHITE_SPACE_REGEX);
        String[] args2 = "--file smalldatafile.txt".split(WHITE_SPACE_REGEX);
        assertEquals("smalldatafile.txt", new CommandLineOptions().processCommandlineOptions(args1).extractFile().getName());
        assertEquals("smalldatafile.txt", new CommandLineOptions().processCommandlineOptions(args2).extractFile().getName());
    }

    @Test
    @DisplayName("should handle sorting commandline option if supplied")
    void should_handle_sorting_commandline_option_if_supplied() throws UserError {
        String[] args1 = "-f smalldatafile.txt -s".split(WHITE_SPACE_REGEX);
        String[] args2 = "-s --file smalldatafile.txt".split(WHITE_SPACE_REGEX);
        String[] args3 = "--file smalldatafile.txt".split(WHITE_SPACE_REGEX);
        assertTrue(new CommandLineOptions().processCommandlineOptions(args1).sortByStartDate());
        assertTrue(new CommandLineOptions().processCommandlineOptions(args2).sortByStartDate());
        assertFalse(new CommandLineOptions().processCommandlineOptions(args3).sortByStartDate());
    }

    @Test
    @DisplayName("should throw error if filter option is used without value")
    void should_throw_error_if_filter_option_is_used_without_value() throws UserError {
        String[] args1 = "-s --file smalldatafile.txt --project".split(WHITE_SPACE_REGEX);
        String[] args2 = "-s -p --file smalldatafile.txt".split(WHITE_SPACE_REGEX);
        Throwable thrown1 = catchThrowable(() -> new CommandLineOptions().processCommandlineOptions(args1).extractFile());
        assertThat(thrown1.getMessage()).isEqualTo(UserError.invalidOptionsError().getMessage());
        Throwable thrown2 = catchThrowable(() -> new CommandLineOptions().processCommandlineOptions(args2).extractFile());
        assertThat(thrown2.getMessage()).isEqualTo(UserError.invalidOptionsError().getMessage());
    }

    @Test
    @DisplayName("should handle project filter commandline option if left out or supplied properly or throw error")
    void should_handle_project_filter_commandline_option_if_left_out_or_supplied_properly_or_throw_error() throws UserError {
        String[] args1 = "-f smalldatafile.txt".split(WHITE_SPACE_REGEX);
        String[] args2 = "--file smalldatafile.txt -p 1".split(WHITE_SPACE_REGEX);
        String[] args3 = "-s --file smalldatafile.txt --project 2".split(WHITE_SPACE_REGEX);
        assertNull(new CommandLineOptions().processCommandlineOptions(args1).filterByProject());
        assertEquals("1", new CommandLineOptions().processCommandlineOptions(args2).filterByProject());
        assertEquals("2", new CommandLineOptions().processCommandlineOptions(args3).filterByProject());
    }

}