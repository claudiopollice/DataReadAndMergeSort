package org.example;

import org.example.process.CommandLineOptions;
import org.example.model.error.ExceptionMapper;
import org.example.process.ProcessOrchestrator;

public class Main {

    public static void main(String[] args) {
        new Main().runProcess(args);
    }

    void runProcess(String[] args) {
        try {
            new ProcessOrchestrator(new CommandLineOptions().processCommandlineOptions(args))
                    .readAndSetFileSize()
                    .setProcessingStrategy()
                    .processInputFileAccordingToStrategy();
        } catch (Exception e) {
            new ExceptionMapper().handle(e);
        }
    }

}


