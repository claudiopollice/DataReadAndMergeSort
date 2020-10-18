package org.example.process;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.example.model.error.SystemError;
import org.example.model.error.UserError;
import org.example.data.StateOnDisk;
import org.example.util.FileReadingUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.example.config.Config.INTERMEDIATE_WRITE_FILE_LINE_NUMBER;
import static org.example.config.Config.maxInputFileSizeInGB;

@Getter
@Setter
public class ProcessOrchestrator {

    private CommandLineOptions options;
    private ProcessingStrategy processingStrategy;
    private Context context;

    public ProcessOrchestrator(@NonNull CommandLineOptions options) throws UserError {
        this.options = options;
        this.context = new Context(options);
    }

    public ProcessOrchestrator readAndSetFileSize() throws UserError, SystemError {
        File inputFile = this.context.getInputFile();
        long fileSize = FileReadingUtil.getFileSize(inputFile);
        if (fileSize <= 0L || fileSize > maxInputFileSizeInGB()) {
            throw UserError.fileSizeError(fileSize, maxInputFileSizeInGB());
        }
        this.context.setInputFileSize(fileSize);
        return this;
    }

    public ProcessOrchestrator setProcessingStrategy() {
        this.processingStrategy = ProcessingStrategy.extractStrategy(this.options, this.context.getInputFileSize());
        return this;
    }

    public void processInputFileAccordingToStrategy() throws IOException, UserError, SystemError {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.context.getInputFile().getPath(), StandardCharsets.UTF_8))) {
            LineProcessor lineProcessor = new LineProcessor(this.context, this.options.filterByProject());
            switch (this.processingStrategy) {
                case READ_AND_PRINT_IMMEDIATELY:
                    readAndPrintWithOptionalFilter(reader, lineProcessor);
                    break;
                case IN_MEMORY_AND_SORT:
                    readAndSortInMemoryWithOptionalFilter(reader, lineProcessor);
                    break;
                case MERGE_SORT:
                    readAndMergeSortWithOptionalFilter(reader, lineProcessor);
                    break;
                default:
                    throw SystemError.processingStrategyError();
            }
        }
    }

    private void readAndPrintWithOptionalFilter(@NonNull BufferedReader reader, @NonNull LineProcessor lineProcessor) throws IOException, UserError {
        String newLine = reader.readLine();
        while (newLine != null) {
            lineProcessor.basicProcessing(newLine).printLineImmediately();
            newLine = reader.readLine();
        }
    }

    private void readAndSortInMemoryWithOptionalFilter(@NonNull BufferedReader reader, @NonNull LineProcessor lineProcessor) throws UserError, IOException {
        String newLine = reader.readLine();
        while (newLine != null) {
            lineProcessor.basicProcessing(newLine).storeInMemory();
            newLine = reader.readLine();
        }
        this.context.getStateInMemory().sortByAscendingStartDate().printEntriesInMemory();
    }

    private void readAndMergeSortWithOptionalFilter(@NonNull BufferedReader reader, @NonNull LineProcessor lineProcessor) throws UserError, IOException, SystemError {
        StateOnDisk stateOnDisk = new StateOnDisk();
        long numberOfIntermediateFiles = readIntoIntermediateFiles(reader, lineProcessor, stateOnDisk);
        stateOnDisk.mergeSortIntermediateFiles(lineProcessor, numberOfIntermediateFiles)
                .printFinalResultAndCleanUp(this.context.getStateInMemory().getInputFileHeader());
    }

    private long readIntoIntermediateFiles(@NonNull BufferedReader reader, @NonNull LineProcessor lineProcessor, @NonNull StateOnDisk stateOnDisk) throws IOException, UserError {
        String newLine = reader.readLine();
        long numberOfIntermediateFiles = 0;
        int lineNumber;
        while (newLine != null) {
            numberOfIntermediateFiles++;
            for (lineNumber = 0; lineNumber < INTERMEDIATE_WRITE_FILE_LINE_NUMBER; lineNumber++) {
                if (newLine != null) {
                    lineProcessor.basicProcessing(newLine).storeInMemory();
                    newLine = reader.readLine();
                }
            }
            stateOnDisk.sortAndWriteToNewIntermediateFile(numberOfIntermediateFiles, this.context.getStateInMemory());
        }
        return numberOfIntermediateFiles;
    }

}
