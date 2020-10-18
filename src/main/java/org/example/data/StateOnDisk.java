package org.example.data;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.example.model.DataEntry;
import org.example.model.error.SystemError;
import org.example.model.error.UserError;
import org.example.process.LineProcessor;
import org.example.util.FileReadingUtil;
import org.javatuples.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.example.config.Config.*;

@Getter
@Setter
public class StateOnDisk {

    private List<File> outputFiles = new ArrayList<>();

    public void sortAndWriteToNewIntermediateFile(long newFileIndex, @NonNull StateInMemory stateInMemory) throws IOException {
        String fileName = INTERMEDIATE_FILE_PREFIX + newFileIndex + INTERMEDIATE_FILE_POSTFIX;
        this.outputFiles.add(new File(fileName));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            List<DataEntry> dataEntries = stateInMemory.sortByAscendingStartDate().getDataEntries();
            for (DataEntry entry : dataEntries) {
                writer.write(String.join(TAB_SEPARATOR, entry.getEntryOutputValues()) + '\n');
            }
        }
        stateInMemory.clear();
    }

    public StateOnDisk mergeSortIntermediateFiles(@NonNull LineProcessor lineProcessor, long numberOfIntermediateFiles) throws UserError, IOException, SystemError {
        boolean keepSorting = true;
        while (keepSorting) {
            keepSorting = mergeSortFilePair(++numberOfIntermediateFiles, lineProcessor);
        }
        return this;
    }

    public void printFinalResultAndCleanUp(@NonNull String header) throws SystemError, IOException {
        if (this.outputFiles.size() != 1) {
            throw this.outputFiles.isEmpty() ? SystemError.noFinalFile() : SystemError.mergeSortIncomplete();
        }
        File file = this.outputFiles.get(0);
        System.out.println(header);
        printContentOfFinalFile(file);
        Files.delete(Path.of(file.getPath()));
        this.outputFiles = new ArrayList<>();
    }

    private boolean mergeSortFilePair(long newFileIndex, @NonNull LineProcessor lineProcessor) throws UserError, SystemError, IOException {
        if (this.outputFiles.size() == 1) {
            return false;
        }
        String mergeSortedFileName = INTERMEDIATE_FILE_PREFIX + newFileIndex + INTERMEDIATE_FILE_POSTFIX;
        File file1 = this.outputFiles.get(0);
        File file2 = this.outputFiles.get(1);
        BufferedReader reader1 = FileReadingUtil.openFileForReading(file1.getName());
        BufferedReader reader2 = FileReadingUtil.openFileForReading(file2.getName());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mergeSortedFileName))) {
            Pair<String, String> lastComparedLines = writeEarliestOccuredFirst(reader1, reader2, lineProcessor, writer);
            Pair<BufferedReader, String>[] readersAndLastReadLines = new Pair[]{new Pair(reader1, lastComparedLines.getValue0()), new Pair(reader2, lastComparedLines.getValue1())};
            writeLeftover(readersAndLastReadLines, writer);
        }
        replaceTwoOldFilesWithOneNew(reader1, reader2, file1, file2, mergeSortedFileName);
        return this.outputFiles.size() > 1;
    }

    private Pair<String, String> writeEarliestOccuredFirst(@NonNull BufferedReader reader1, @NonNull BufferedReader reader2, @NonNull LineProcessor lineProcessor, BufferedWriter writer) throws UserError, IOException {
        String line1 = reader1.readLine();
        String line2 = reader2.readLine();

        while (line1 != null && line2 != null) {
            boolean line1IsEarlier = lineProcessor.hasStartDateEarlierThen(line1, line2);
            if (line1IsEarlier) {
                writer.write(line1 + '\n');
                line1 = reader1.readLine();
            } else {
                writer.write(line2 + '\n');
                line2 = reader2.readLine();
            }
        }
        return new Pair(line1, line2);
    }

    private void writeLeftover(@NonNull Pair<BufferedReader, String>[] readersAndLastReadLines, BufferedWriter writer) throws IOException {
        for (Pair<BufferedReader, String> pair : readersAndLastReadLines) {
            BufferedReader reader = pair.getValue0();
            String line = pair.getValue1();
            while (line != null) {
                writer.write(line + '\n');
                line = reader.readLine();
            }
        }
    }

    private void replaceTwoOldFilesWithOneNew(@NonNull BufferedReader reader1, @NonNull BufferedReader reader2, @NonNull File file1, @NonNull File file2, @NonNull String mergeSortedFileName) throws IOException {
        reader1.close();
        reader2.close();
        this.outputFiles.remove(file1);
        this.outputFiles.remove(file2);
        Files.delete(Path.of(file1.getPath()));
        Files.delete(Path.of(file2.getPath()));
        this.outputFiles.add(new File(mergeSortedFileName));
    }

    private void printContentOfFinalFile(@NonNull File file) throws SystemError, IOException {
        BufferedReader reader = FileReadingUtil.openFileForReading(file.getName());
        String line = reader.readLine();
        while (line != null) {
            System.out.println(line);
            line = reader.readLine();
        }
    }

}
