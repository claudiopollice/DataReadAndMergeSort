package org.example.process;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.example.model.error.SystemError;
import org.example.model.error.UserError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.example.TestUtil.newProcessOrchestrator;

class ProcessOrchestratorTest {

    @Test
    @DisplayName("should set options and create context with correct input file upon creation")
    void should_set_options_and_create_context_with_correct_input_file_upon_creation() throws UserError {
        ProcessOrchestrator processOrchestrator = newProcessOrchestrator("--file smalldatafile.txt");
        assertEquals("smalldatafile.txt", processOrchestrator.getOptions().getFilePath());
        assertEquals("smalldatafile.txt", processOrchestrator.getContext().getInputFile().getName());
    }

    @Test
    @DisplayName("should set input file size in the context")
    void should_set_input_file_size_in_the_context() throws UserError, SystemError {
        ProcessOrchestrator processOrchestrator = newProcessOrchestrator("--file smalldatafile.txt");
        processOrchestrator.readAndSetFileSize();
        assertEquals(1097L, processOrchestrator.getContext().getInputFileSize());
    }

    @Test
    @DisplayName("should set correct processing strategy for small file")
    void should_set_correct_processing_strategy_for_small_file() throws UserError, SystemError {
        ProcessOrchestrator processOrchestrator1 = newProcessOrchestrator("--file smalldatafile.txt")
                .readAndSetFileSize()
                .setProcessingStrategy();
        assertEquals(ProcessingStrategy.READ_AND_PRINT_IMMEDIATELY, processOrchestrator1.getProcessingStrategy());

        ProcessOrchestrator processOrchestrator2 = newProcessOrchestrator("--file smalldatafile.txt -s")
                .readAndSetFileSize()
                .setProcessingStrategy();
        assertEquals(ProcessingStrategy.IN_MEMORY_AND_SORT, processOrchestrator2.getProcessingStrategy());
    }
}