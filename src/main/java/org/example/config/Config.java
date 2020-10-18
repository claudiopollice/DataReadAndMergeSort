package org.example.config;

public class Config {

    // input file size
    private static final int MAX_FILE_SIZE_IN_MEMORY_IN_MB = 20;
    private static final int MAX_INPUT_FILE_SIZE_IN_GB = 2;

    // input file format
    public static final String COMMENT_PREFIX = "#";
    public static final String NULL_VALUE = "NULL";
    public static final String NULL_REPLACEMENT_VALUE = " ";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String TAB_SEPARATOR = "\t";
    public static final int INPUT_FILE_COLUMNS_NUMBER = 8;

    // intermediate writes
    public static final int INTERMEDIATE_WRITE_FILE_LINE_NUMBER_IN_THOUSANDS = 200;
    public static final String INTERMEDIATE_FILE_PREFIX = "intermediate-output-file";
    public static final String INTERMEDIATE_FILE_POSTFIX = ".txt";
    public static final int INTERMEDIATE_WRITE_FILE_LINE_NUMBER = INTERMEDIATE_WRITE_FILE_LINE_NUMBER_IN_THOUSANDS * 1000; // rough estimate = 1000 lines ~ 1 MB file

    public static long maxInputFileSizeInGB() {
        return MAX_INPUT_FILE_SIZE_IN_GB * 1024 * 1024 * 1024L;
    }

    public static long maxFileSizeInMemoryInMB() {
        return MAX_FILE_SIZE_IN_MEMORY_IN_MB * 1024 * 1024L;
    }
}
