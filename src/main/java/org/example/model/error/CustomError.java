package org.example.model.error;

import lombok.NonNull;

public interface CustomError {

    String SYSTEM_ERROR_MESSAGE_PREFIX = "SYSTEM ERROR -- ";
    String USER_ERROR_MESSAGE_PREFIX = "USER ERROR -- ";

    static void handle(@NonNull CustomError error) {
        System.out.println(getPrefix(error).concat(((Exception) error).getMessage()));
    }

    private static String getPrefix(@NonNull CustomError error) {
        return error instanceof SystemError ? SYSTEM_ERROR_MESSAGE_PREFIX : USER_ERROR_MESSAGE_PREFIX;
    }

}
