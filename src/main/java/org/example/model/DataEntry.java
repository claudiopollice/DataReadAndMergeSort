package org.example.model;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.example.model.error.UserError;
import org.example.util.LocalDateTimeParser;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static org.example.config.Config.NULL_REPLACEMENT_VALUE;
import static org.example.config.Config.NULL_VALUE;

@Getter
@Setter
@NoArgsConstructor
public class DataEntry implements Comparable<DataEntry> {

    private LocalDateTime startDate;
    private List<String> entryOutputValues = new ArrayList<>(Collections.emptyList());

    public DataEntry fromInputLine(@NonNull List<String> valuesInLine, ImmutableMap<Integer, String> columnOrderMap, long lineIndex) throws UserError {
        for (String value : valuesInLine) {
            try {
                DataColumn column = DataColumn.fromString(columnOrderMap.get(valuesInLine.indexOf(value)));
                process(column, value);
            } catch (UserError e) {
                throw e.appendLineIndex(lineIndex);
            } catch (DateTimeException dte) {
                throw UserError.wrongStartDateFormatError(valuesInLine, value, lineIndex);
            } catch (IllegalArgumentException iae) {
                throw UserError.nonCompliantValue(value);
            }
        }
        return this;
    }

    @Override
    public int compareTo(DataEntry dataEntry) {
        return this.getStartDate().compareTo(dataEntry.getStartDate());
    }

    private void process(@NonNull DataColumn column, @NonNull String value) throws UserError {
        String validatedValue = validate(column, value);
        if (DataColumn.START_DATE.equals(column)) {
            processAsDate.accept(validatedValue, this);
        } else {
            processAsIs.accept(validatedValue, this);
        }
    }

    private String validate(@NonNull DataColumn column, @NonNull String value) throws UserError {
        String nonEmptyValue = validateNotEmpty(value);
        switch (column) {
            case SAVINGS_AMOUNT:
                return validateSavingsAmount(nonEmptyValue);
            case CURRENCY:
                return replaceNullWithEmpty(nonEmptyValue);
            case COMPLEXITY:
                return Complexity.fromString(nonEmptyValue).toString();
            default:
                return nonEmptyValue;
        }
    }

    private static String validateNotEmpty(@NonNull String value) throws UserError {
        String trimmedValue = value.trim();
        if (StringUtils.isEmpty(trimmedValue)) {
            throw UserError.emptyValueError();
        }
        return trimmedValue;
    }

    private String validateSavingsAmount(@NonNull String rawSavingsAmount) throws UserError {
        String savingsAmount = replaceNullWithEmpty(rawSavingsAmount);
        return StringUtils.isBlank(savingsAmount) ? savingsAmount : validateNumberFormat(savingsAmount);
    }

    private String validateNumberFormat(@NonNull String savingsAmount) throws UserError {
        try {
            return new BigDecimal(savingsAmount).toString();
        } catch (NumberFormatException e) {
            throw UserError.invalidNumberError(savingsAmount);
        }
    }

    private String replaceNullWithEmpty(@NonNull String value) {
        return value.equals(NULL_VALUE) ? NULL_REPLACEMENT_VALUE : value;
    }

    private static final BiConsumer<String, DataEntry> processAsIs = (value, dataEntry) -> dataEntry.getEntryOutputValues().add(value);
    private static final BiConsumer<String, DataEntry> processAsDate = (startDateString, dataEntry) -> {
        LocalDateTime ldt = LocalDateTimeParser.parseStartDate(startDateString);
        dataEntry.setStartDate(ldt);
        dataEntry.getEntryOutputValues().add(LocalDateTimeParser.toString(ldt));
    };

}
