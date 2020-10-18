package org.example.data;

import lombok.Getter;
import lombok.Setter;
import org.example.model.DataEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.example.config.Config.TAB_SEPARATOR;

@Getter
@Setter
public class StateInMemory {

    private String inputFileHeader;
    private List<DataEntry> dataEntries;

    public StateInMemory() {
        this.dataEntries = new ArrayList<>();
    }

    public StateInMemory sortByAscendingStartDate() {
        Collections.sort(dataEntries);
        return this;
    }

    public void clear() {
        this.dataEntries = new ArrayList<>();
    }

    public void printEntriesInMemory() {
        System.out.println(this.getInputFileHeader());
        this.getDataEntries()
                .forEach(dataEntry -> System.out.println(String.join(TAB_SEPARATOR, dataEntry.getEntryOutputValues())));
    }
}
