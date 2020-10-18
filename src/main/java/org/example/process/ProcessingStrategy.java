package org.example.process;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import static org.example.config.Config.maxFileSizeInMemoryInMB;
import static org.example.process.ProcessingStrategy.ProcessingStrategyValues.*;


@Getter
@AllArgsConstructor
public enum ProcessingStrategy {

    READ_AND_PRINT_IMMEDIATELY(IN_MEMORY_VALUE),
    IN_MEMORY_AND_SORT(IN_MEMORY_VALUE + SORT_VALUE),
    MERGE_SORT(INTERMEDIATE_WRITES_VALUE + SORT_VALUE);

    private int strategyNumber;

    static ProcessingStrategy extractStrategy(@NonNull CommandLineOptions options, long fileSizeInMB) {
        return IN_MEMORY_AND_SORT.shouldSort(options).shouldUseIntermediateWrites(fileSizeInMB);
    }

    private ProcessingStrategy shouldSort(@NonNull CommandLineOptions options) {
        return options.sortByStartDate() ? this : READ_AND_PRINT_IMMEDIATELY;
    }

    private ProcessingStrategy shouldUseIntermediateWrites(long fileSizeInMB) {
        return this.getStrategyNumber() / SORT_VALUE == 1 && fileSizeInMB > maxFileSizeInMemoryInMB()
                ? this.add(-IN_MEMORY_VALUE + INTERMEDIATE_WRITES_VALUE)
                : this;
    }

    private ProcessingStrategy add(int addition) {
        return strategyFromValueMap.get(this.getStrategyNumber() + addition);
    }

    static class ProcessingStrategyValues {
        static final int IN_MEMORY_VALUE = 1;
        static final int INTERMEDIATE_WRITES_VALUE = 2;
        static final int SORT_VALUE = 10;
    }

    private static ImmutableMap<Integer, ProcessingStrategy> strategyFromValueMap = ImmutableMap
            .<Integer, ProcessingStrategy>builder()
            .put(IN_MEMORY_VALUE, READ_AND_PRINT_IMMEDIATELY)
            .put(IN_MEMORY_VALUE + SORT_VALUE, IN_MEMORY_AND_SORT)
            .put(INTERMEDIATE_WRITES_VALUE + SORT_VALUE, MERGE_SORT)
            .build();

}