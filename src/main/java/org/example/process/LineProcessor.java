package org.example.process;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.example.model.DataColumn;
import org.example.model.error.UserError;
import org.example.model.DataEntry;

import java.util.List;
import java.util.stream.Collectors;

import static org.example.config.Config.*;
import static org.example.util.LocalDateTimeParser.parseStartDate;

@Getter
@Setter
@NoArgsConstructor
public class LineProcessor {


    private Context context;
    private String currentLine;
    private List<String> valuesInCurrentLine;
    private DataEntry currentDataEntry;
    private String filterProject;
    private long lineIndex = 0;
    private boolean headerIsProcessed = false;
    private boolean currentLineIsProcessed = false;

    public LineProcessor(@NonNull Context context, String filterProject) {
        this.context = context;
        this.filterProject = filterProject;
    }

    public LineProcessor basicProcessing(@NonNull String newLine) throws UserError {
        return this
                .startOnLine(newLine)
                .checkForCommentsOrEmpty()
                .checkForHeaderLine()
                .filter()
                .transformInput();
    }

    public void printLineImmediately() {
        if (!currentLineIsProcessed && this.headerIsProcessed) {
            System.out.println(String.join(TAB_SEPARATOR, this.currentDataEntry.getEntryOutputValues()));
            currentLineIsProcessed = true;
        }
    }

    public void storeInMemory() {
        if (!currentLineIsProcessed && this.headerIsProcessed) {
            this.context.getStateInMemory().getDataEntries().add(this.currentDataEntry);
        }
        this.currentLineIsProcessed = true;
    }

    public boolean hasStartDateEarlierThen(@NonNull String line1, @NonNull String line2) throws UserError {
        String startDateString1 = breakUpInputLineIntoValues(line1).get(this.context.getDateColumn());
        String startDateString2 = breakUpInputLineIntoValues(line2).get(this.context.getDateColumn());
        return parseStartDate(startDateString1).isBefore(parseStartDate(startDateString2));
    }

    private LineProcessor startOnLine(@NonNull String newLine) {
        this.currentLine = newLine;
        this.lineIndex++;
        this.currentLineIsProcessed = false;
        return this;
    }

    private LineProcessor checkForCommentsOrEmpty() throws UserError {
        this.currentLine = this.currentLine.trim();
        this.currentLineIsProcessed = this.currentLine.isEmpty() || this.currentLine.startsWith(COMMENT_PREFIX);
        this.valuesInCurrentLine = this.currentLineIsProcessed ? null : breakUpInputLineIntoValues(this.currentLine);
        return this;
    }

    private LineProcessor checkForHeaderLine() throws UserError {
        if (!this.currentLineIsProcessed && !this.headerIsProcessed) {
            setColumnOrderMap(this.currentLine);
            context.allColumnsPresent();
            setFileHeaderLine(context);
            this.headerIsProcessed = true;
            currentLineIsProcessed = true;
        }
        return this;
    }

    private LineProcessor filter() {
        if (!this.currentLineIsProcessed && this.headerIsProcessed) {
            this.currentLineIsProcessed = !StringUtils.isEmpty(this.filterProject) && !this.valuesInCurrentLine.get(this.context.getProjectColumn()).equals(filterProject);
        }
        return this;
    }

    private LineProcessor transformInput() throws UserError {
        if (!currentLineIsProcessed && this.headerIsProcessed) {
            this.currentDataEntry = new DataEntry().fromInputLine(this.valuesInCurrentLine, this.context.getColumnOrderMap(), this.lineIndex);
        }
        return this;
    }

    private List<String> breakUpInputLineIntoValues(@NonNull String line) throws UserError {
        String[] values = line.split(TAB_SEPARATOR);
        int amountOfColumns = values.length;
        if (amountOfColumns != INPUT_FILE_COLUMNS_NUMBER) {
            throw UserError.wrongColumnNumberInLineError(amountOfColumns, this.lineIndex, this.currentLine);
        }
        return List.of(values);
    }

    private LineProcessor setColumnOrderMap(String headerLine) throws UserError {
        context.populateColumnOrderMap(headerLine, this.lineIndex);
        this.currentLineIsProcessed = true;
        return this;
    }

    private void setFileHeaderLine(@NonNull Context context) {
        context.getStateInMemory().setInputFileHeader(context.getColumnsSet()
                .stream()
                .map(DataColumn::toString)
                .collect(Collectors.joining(TAB_SEPARATOR)));
    }
}
