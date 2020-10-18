package org.example.util;

import lombok.NonNull;
import org.example.model.error.SystemError;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class FileReadingUtil {

    public static long getFileSize(@NonNull File file) throws SystemError {
        long fileSize;
        try (FileChannel fileChannel = (FileChannel) Files.newByteChannel(Path.of(file.getPath()),
                EnumSet.of(StandardOpenOption.READ))) {
            fileSize = fileChannel.size();
        } catch (IOException e) {
            throw SystemError.fileReadingError(file.getName());
        }
        return fileSize;
    }

    public static BufferedReader openFileForReading(String fileName) throws SystemError {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            throw SystemError.fileNotFoundError(fileName);
        }
        return bufferedReader;
    }

}
