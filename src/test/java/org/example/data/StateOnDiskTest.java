package org.example.data;

import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.example.model.error.SystemError;
import org.example.model.error.UserError;
import org.example.process.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.example.TestUtil.*;
import static org.example.config.Config.INTERMEDIATE_FILE_POSTFIX;
import static org.example.config.Config.INTERMEDIATE_FILE_PREFIX;
import static org.example.util.FileReadingUtil.openFileForReading;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StateOnDiskTest {

    @Test
    @DisplayName("should sort and write to intermediate file")
    void should_sort_and_write_to_intermediate_file() throws UserError, IOException, SystemError {
        Context context = prepareColumnOrderMap(context());
        StateInMemory stateInMemory = fillMemory(LINES_2014_2013_2015_2011, context);
        new StateOnDisk().sortAndWriteToNewIntermediateFile(1, stateInMemory);

        assertResultedFileThenDelete(1, LINES_2011_2013_2014_2015_TRANSFORMED);
    }

    @Test
    @DisplayName("should merge sort two files, writing the result to a third file and delete the first two")
    void should_merge_sort_two_files_writing_the_result_to_a_third_file_and_delete_the_first_two() throws UserError, IOException, SystemError {
        StateOnDisk stateOnDisk = new StateOnDisk();

        Context context1 = prepareColumnOrderMap(context());
        StateInMemory stateInMemory1 = fillMemory(LINES_2014_2013_2015_2011, context1);
        stateOnDisk.sortAndWriteToNewIntermediateFile(1, stateInMemory1);

        Context context2 = prepareColumnOrderMap(context());
        StateInMemory stateInMemory2 = fillMemory(LINES_2016_2000_2012_2018, context2);
        stateOnDisk.sortAndWriteToNewIntermediateFile(2, stateInMemory2);

        stateOnDisk.mergeSortIntermediateFiles(newLineProcesser(true, false, null), 2);

        assertResultedFileThenDelete(3, LINES_2000_2011_2012_2013_2014_2015_2016_2018_TRANSFORMED);
    }

    @Test
    @DisplayName("should delete final result file after printing final result")
    void should_delete_final_result_file_after_printing_final_result() throws IOException, UserError, SystemError {
        StateOnDisk stateOnDisk = new StateOnDisk();

        Context context = prepareColumnOrderMap(context());
        StateInMemory stateInMemory = fillMemory(LINES_2014_2013_2015_2011, context);
        stateInMemory.setInputFileHeader(HEADER_LINE);
        stateOnDisk.sortAndWriteToNewIntermediateFile(1, stateInMemory);
        stateOnDisk.printFinalResultAndCleanUp(HEADER_LINE);

        String fileName = INTERMEDIATE_FILE_PREFIX + 1 + INTERMEDIATE_FILE_POSTFIX;
        Throwable thrown = catchThrowable(() -> openFileForReading(fileName));
        assertTrue(thrown instanceof SystemError);
        assertEquals(thrown.getMessage(), SystemError.fileNotFoundError(fileName).getMessage());
    }

    private void assertResultedFileThenDelete(int fileIndex, @NonNull List<String> expectedFileContent) throws SystemError, IOException {
        String fileName = INTERMEDIATE_FILE_PREFIX + fileIndex + INTERMEDIATE_FILE_POSTFIX;
        BufferedReader reader = openFileForReading(fileName);
        List<String> linesInFile = new ArrayList<>();
        String line = reader.readLine();
        while (line != null) {
            linesInFile.add(line);
            line = reader.readLine();
        }

        for (int i = 0; i < linesInFile.size(); i++) {
            assertEquals(linesInFile.get(i), expectedFileContent.get(i));
        }

        Files.delete(Path.of(fileName));
    }

}