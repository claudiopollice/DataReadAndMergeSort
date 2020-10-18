package org.example.process;

import lombok.Getter;
import org.example.model.error.UserError;
import picocli.CommandLine;

import java.io.File;
import java.util.Objects;

import static org.example.model.error.UserError.invalidOptionsError;

@Getter
public class CommandLineOptions {


    @CommandLine.Option(names = {"-f", "--file"}, required = true)
    private String filePath;

    @CommandLine.Option(names = {"-s", "--sortByStartDate"})
    private Boolean sortByStartDate = Boolean.FALSE;

    @CommandLine.Option(names = {"-p", "--project"})
    private String project;


    public CommandLineOptions processCommandlineOptions(String[] args) throws UserError {
        try {
            new CommandLine(this).parseArgs(args);
        } catch (Exception e) {
            throw invalidOptionsError();
        }
        return this;
    }

    public File extractFile() throws UserError {
        File inputFile = new File(this.getFilePath());
        if (inputFile.length() == 0) {
            throw UserError.missingFileError();
        }
        inputFile.setReadOnly();
        return inputFile;
    }

    public boolean sortByStartDate() {
        return Objects.equals(this.sortByStartDate, Boolean.TRUE);
    }

    public String filterByProject() {
        return this.project;
    }
}
