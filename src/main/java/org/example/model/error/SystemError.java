package org.example.model.error;

import lombok.NonNull;

public class SystemError extends Exception implements CustomError {

    private static final long serialVersionUID = -4675799783391379392L;

    public SystemError(String errorMessage) {
        super(errorMessage);
    }

    public static SystemError fileReadingError(@NonNull String fileName) {
        return new SystemError("Error occured while reading file with name: " + fileName + ".");
    }

    public static SystemError fileNotFoundError(@NonNull String fileName) {
        return new SystemError("Tried to read file with name " + fileName + " but could not find file on disk.");
    }

    public static SystemError processingStrategyError() {
        return new SystemError("Failed creating strategy for processing.");
    }

    public static SystemError noFinalFile() {
        return new SystemError("Tried to print final result but no final file was found.");
    }

    public static SystemError mergeSortIncomplete() {
        return new SystemError("Tried to print final result but merge sort has not completed.");
    }
}
