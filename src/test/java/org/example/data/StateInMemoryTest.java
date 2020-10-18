package org.example.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.example.model.error.UserError;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.example.TestUtil.*;

class StateInMemoryTest {

    @Test
    @DisplayName("should sort by ascending orderr")
    void should_sort_by_ascending_order() throws UserError {
        StateInMemory stateInMemory = fillMemory(LINES_2014_2013_2015_2011, prepareColumnOrderMap(context()));
        stateInMemory.sortByAscendingStartDate();

        assertAll("assert in ascending order",
                () -> assertEquals(LINE_4_2011, String.join(TAB, stateInMemory.getDataEntries().get(0).getEntryOutputValues())),
                () -> assertEquals(LINE_2_2013, String.join(TAB, stateInMemory.getDataEntries().get(1).getEntryOutputValues())),
                () -> assertEquals(LINE_1_2014_NULL_REPLACED, String.join(TAB, stateInMemory.getDataEntries().get(2).getEntryOutputValues())),
                () -> assertEquals(LINE_3_2015, String.join(TAB, stateInMemory.getDataEntries().get(3).getEntryOutputValues()))
        );
    }

    @Test
    @DisplayName("should clear state in memory")
    void should_clear_state_in_memory() throws UserError {
        StateInMemory stateInMemory = new StateInMemory();
        stateInMemory.getDataEntries().add(dataEntry(LINE_2_2013));
        stateInMemory.clear();
        assertEquals(0, stateInMemory.getDataEntries().size());
    }

}