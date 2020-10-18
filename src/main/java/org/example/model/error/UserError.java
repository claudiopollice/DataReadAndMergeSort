package org.example.model.error;

import lombok.NonNull;
import org.example.config.Config;

import java.util.List;

import static org.example.config.Config.TAB_SEPARATOR;

public class UserError extends Exception implements CustomError {

    private static final long serialVersionUID = -4675799783391379392L;
    private static final String APPEND_FORMAT = "%n%s %s.";

    public UserError(String errorMessage) {
        super(errorMessage);
    }

    public static UserError invalidOptionsError() {
        return new UserError("Invalid options. Use -f or --file option to supply file followed by file name. Use -p or --project followed by project name to filter on project.");
    }

    public static UserError missingFileError() {
        return new UserError("File does not exist or is empty.");
    }

    public static UserError wrongStartDateFormatError(@NonNull List<String> valuesInLine, String nonCompliantValue, long lineIndex) {
        return new UserError(String.format("%s %s.", "Start date has to be of format", Config.DATE_TIME_PATTERN))
                .appendNonCompliantValue(nonCompliantValue)
                .appendLineContent(String.join(TAB_SEPARATOR, valuesInLine))
                .appendLineIndex(lineIndex);
    }

    public static UserError corruptHeaderLineError(@NonNull String headerLine, @NonNull String nonCompliantValue, long lineIndex) {
        return new UserError("First non commented line must be valid header line.")
                .appendNonCompliantValue(nonCompliantValue)
                .appendLineContent(headerLine)
                .appendLineIndex(lineIndex);
    }

    public static UserError columnValidationError(@NonNull List<String> missingColumns) {
        String errorMessage = "Missing the following columns in the header: " + String.join(", ", missingColumns);
        return new UserError(errorMessage);
    }

    public static UserError invalidNumberError(String savingsAmount) {
        return new UserError("Savings amount values can only contain numbers and one decimal point.")
                .appendNonCompliantValue("Non-compliant value: " + savingsAmount);
    }

    public static UserError nonCompliantValue(@NonNull String nonCompliantValue) {
        String errorMessage = String.format("%s %s.","Non-compliant value:", nonCompliantValue);
        return new UserError(errorMessage);
    }

    public static UserError emptyValueError() {
        String errorMessage = "Empty values are not allowed.";
        return new UserError(errorMessage);
    }

    public static UserError fileSizeError(long fileSize, long maxFileSize) {
        return fileSize > maxFileSize ? tooLargeFileError(maxFileSize) : emptyFileError();
    }

    public static UserError wrongColumnNumberInLineError(int amountOfColumns, long lineIndex, String currentLine) {
        return new UserError(String.format("Expected to find 8 values in each line, but instead found %s values", amountOfColumns))
                .appendLineContent(currentLine)
                .appendLineIndex(lineIndex);

    }

    public UserError appendLineIndex(long lineIndex) {
        String message = "Error occured in line:";
        return new UserError(this.getMessage().concat(String.format(APPEND_FORMAT, message, lineIndex)));
    }

    private UserError appendLineContent(String line) {
        String message = "Line content:";
        return new UserError(this.getMessage().concat(String.format(APPEND_FORMAT, message, line)));
    }

    private UserError appendNonCompliantValue(String nonCompliantValue) {
        String message = "Non-compliant value:";
        return new UserError(this.getMessage().concat(String.format(APPEND_FORMAT, message, nonCompliantValue)));
    }

    private static UserError emptyFileError() {
        return new UserError("File is empty.");
    }

    private static UserError tooLargeFileError(long maxFileSize) {
        return new UserError(String.format("%s %s", "Input file exceeds allowance. Max input file size is", maxFileSizeString(maxFileSize)));
    }

    private static String maxFileSizeString(long byteSize) {
        long kiloByteSize = byteSize / 1024;
        long megaByteSize = kiloByteSize / 1024;
        long gigaByteSize = megaByteSize / 1024;
        long teraByteSize = gigaByteSize / 1024;

        if (teraByteSize > 0) {
            return teraByteSize + " TB.)";
        } else if (gigaByteSize > 0) {
            return gigaByteSize + " GB.)";
        } else if (megaByteSize > 0) {
            return megaByteSize + " MB.)";
        } else if (kiloByteSize > 0) {
            return kiloByteSize + " KB.)";
        } else {
            return byteSize + " bytes.";
        }
    }

}
