package org.example.process;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.javatuples.Pair;
import org.example.data.StateInMemory;
import org.example.model.DataColumn;
import org.example.model.error.UserError;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.example.config.Config.TAB_SEPARATOR;

@Getter
@Setter
public class Context {

    private File inputFile;
    private long inputFileSize;
    private StateInMemory stateInMemory;
    private ImmutableMap<Integer, String> columnOrderMap;
    private Set<DataColumn> columnsSet = new HashSet();
    private Integer projectColumn;
    private Integer dateColumn;

    public Context(@NonNull CommandLineOptions options) throws UserError {
        this.inputFile = options.extractFile();
        this.stateInMemory = new StateInMemory();
    }

    public void populateColumnOrderMap(@NonNull String headerLine, long lineIndex) throws UserError {
        List<Pair<Integer, String>> pairs = new ArrayList<>();
        int index = 0;
        for (String columnName : List.of(headerLine.split(TAB_SEPARATOR))) {
            try {
                columnsSet.add(DataColumn.fromString(columnName));
            } catch (IllegalArgumentException iae) {
                throw UserError.corruptHeaderLineError(headerLine, columnName, lineIndex);
            }
            updateContext(columnName, index);
            pairs.add(new Pair<>(index, columnName));
            index++;
        }
        this.columnOrderMap = toImmutableMap(pairs);
    }

    public void allColumnsPresent() throws UserError {
        AtomicBoolean allColumnsPresent = new AtomicBoolean(true);
        List<String> missingColumns = new ArrayList<>();
        List.of(DataColumn.values())
                .forEach(column -> {
                    if (!this.columnsSet.contains(column) || !this.columnOrderMap.containsValue(column.toString())) {
                        allColumnsPresent.set(false);
                        missingColumns.add(column.toString());
                    }
                });
        if (!allColumnsPresent.get()) {
            throw UserError.columnValidationError(missingColumns);
        }
    }

    private void updateContext(@NonNull String columnName, int index) {
        if (DataColumn.PROJECT.toString().equals(columnName)) {
            this.projectColumn = index;
        }
        if (DataColumn.START_DATE.toString().equals(columnName)) {
            this.dateColumn = index;
        }
    }

    private ImmutableMap<Integer, String> toImmutableMap(@NonNull List<Pair<Integer, String>> pairs) {
        return pairs.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Pair::getValue0, Pair::getValue1),
                        ImmutableMap::copyOf));
    }
}
