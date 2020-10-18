package org.example;

import com.google.common.collect.ImmutableMap;
import lombok.NonNull;
import org.example.model.DataColumn;
import org.example.process.CommandLineOptions;
import org.example.process.Context;
import org.example.data.StateInMemory;
import org.example.model.DataEntry;
import org.example.model.error.UserError;
import org.example.process.LineProcessor;
import org.example.process.ProcessOrchestrator;

import java.util.List;

import static org.example.config.Config.TAB_SEPARATOR;

public class TestUtil {

    public static final String WHITE_SPACE_REGEX = "\\s";
    public static final String TAB = "\t";
    public static final String COMMENTED_LINE = "#\tThis\tis\ta\tcommented\tline";
    public static final String EMPTY_LINE = "";
    public static final String WHITE_SPACE_LINE = "   \t   ";
    public static final String INVALID_DATE = "2013/01/01 00:00:00.000";
    public static final String INVALID_SAVINGS_AMOUNT = "24,69";
    public static final String NOT_ALLOWED_COMPLEXITY = "Very High";
    public static final String HEADER_LINE = String.join(TAB, "Project", "Description", "Start date", "Category", "Responsible", "Savings amount", "Currency", "Complexity");
    public static final String HEADER_LINE_DIFFERENT_ORDER = String.join(TAB, "Description", "Project", "Category", "Start date", "Responsible", "Savings amount", "Currency", "Complexity");
    public static final String HEADER_LINE_MISSING_COMPLEXITY = "Project\tDescription\tStart date\tCategory\tResponsible\tSavings amount\tCurrency";
    public static final String HEADER_LINE_WITH_ILLEGAL_VALUE = "Project\tDescription\tStart date\tCategory\tResponsible\tSavings amount\tCurrency\tComplexityy";
    public static final String LINE_1_2014 = "2\tHarmonize Lactobacillus acidophilus sourcing\t2014-01-01 00:00:00.000\tDairy\tDaisy Milks\tNULL\tNULL\tSimple";
    public static final String LINE_2_2013 = "3\tSubstitute Crème fraîche with evaporated milk in ice-cream products\t2013-01-01 00:00:00.000\tDairy\tDaisy Milks\t141415.942696\tEUR\tModerate";
    public static final String LINE_3_2015 = "3\tSubstitute Crème fraîche with evaporated milk in ice-cream products\t2015-01-01 00:00:00.000\tDairy\tDaisy Milks\t141415.942696\tEUR\tModerate";
    public static final String LINE_4_2011 = "4\tDecrease production related non-categorized side costs\t2011-01-01 00:00:00.000\tDairy\tDaisy Milks\t11689.322459\tEUR\tHazardous";
    public static final String LINE_5_2016 = "4\tDecrease production related non-categorized side costs\t2016-01-01 00:00:00.000\tDairy\tDaisy Milks\t11689.322459\tEUR\tHazardous";
    public static final String LINE_6_2000 = "5\tStop using Kryptonite in production\t2000-04-01 00:00:00.000\tDairy\tClark Kent\tNULL\tNULL\tModerate";
    public static final String LINE_7_2012 = "6\tBlack and white logo paper\t2012-06-01 00:00:00.000\tOffice supplies\tClark Kent\t4880.199567\tEUR\tSimple";
    public static final String LINE_8_2018 = "6\tBlack and white logo paper\t2018-06-01 00:00:00.000\tOffice supplies\tClark Kent\t4880.199567\tEUR\tSimple";
    public static final String LINE_1_2014_NULL_REPLACED = "2\tHarmonize Lactobacillus acidophilus sourcing\t2014-01-01 00:00:00.000\tDairy\tDaisy Milks\t \t \tSimple";
    public static final String LINE_1_2000_NULL_REPLACED = "5\tStop using Kryptonite in production\t2000-04-01 00:00:00.000\tDairy\tClark Kent\t \t \tModerate";
    public static final List<String> LINES_2014_2013_2015_2011 = List.of(LINE_1_2014, LINE_2_2013, LINE_3_2015, LINE_4_2011);
    public static final List<String> LINES_2011_2013_2014_2015_TRANSFORMED = List.of(LINE_4_2011, LINE_2_2013, LINE_1_2014_NULL_REPLACED, LINE_3_2015);
    public static final List<String> LINES_2016_2000_2012_2018 = List.of(LINE_5_2016, LINE_6_2000, LINE_7_2012, LINE_8_2018);
    public static final List<String> LINES_2000_2011_2012_2013_2014_2015_2016_2018_TRANSFORMED = List.of(LINE_1_2000_NULL_REPLACED, LINE_4_2011, LINE_7_2012, LINE_2_2013, LINE_1_2014_NULL_REPLACED, LINE_3_2015, LINE_5_2016, LINE_8_2018);
    public static final String LINE_WITH_VALUES_DIFFERENT_ORDER = String.join(TAB, "val1", "val2", "val4", "2013-01-01 00:00:00.000", "val5", "24.69", "val7", "Simple");
    public static final String LINE_WITH_EMPTY_VALUES = String.join(TAB, "val1", "val2", "2013-01-01 00:00:00.000", "val4", "val5", "", "", "Simple");
    public static final String LINE_WITH_NULL_VALUES = String.join(TAB, "val1", "val2", "2013-01-01 00:00:00.000", "val4", "val5", "NULL", "NULL", "Simple");
    public static final String LINE_WITH_NULL_VALUES_REPLACED = String.join(TAB, "val1", "val2", "2013-01-01 00:00:00.000", "val4", "val5", " ", " ", "Simple");
    public static final String LINE_WITH_9_VALUES = String.join(TAB, "val1", "val2", "2013-01-01 00:00:00.000", "val4", "val5", "24.69", "val7", "Simple", "val 9");
    public static final String LINE_WITH_7_VALUES = String.join(TAB, "val1", "val2", "2013-01-01 00:00:00.000", "val4", "val5", "24.69", "val7");
    public static final String LINE_WITH_WRONG_DATE_FORMAT = String.join(TAB, "val1", "val2", INVALID_DATE, "val4", "val5", "24.69", "val7", "Simple");
    public static final String LINE_WITH_WRONG_MONEY_FORMAT = String.join(TAB, "val1", "val2", "2013-01-01 00:00:00.000", "val4", "val5", INVALID_SAVINGS_AMOUNT, "val7", "Simple");
    public static final String LINE_WITH_NOT_ALLOWED_COMPLEXITY = String.join(TAB, "val1", "val2", "2013-01-01 00:00:00.000", "val4", "val5", "24.69", "val7", NOT_ALLOWED_COMPLEXITY);

    public static Context context() throws UserError {
        String[] args = "--file smalldatafile.txt".split(WHITE_SPACE_REGEX);
        CommandLineOptions options = new CommandLineOptions().processCommandlineOptions(args);
        return new Context(options);
    }

    public static LineProcessor newLineProcesser(boolean sort, boolean filter, String filterValue) throws UserError {
        String args = "-f smalldatafile.txt";
        args = sort ? args.concat(" -s") : args;
        args = filter ? args.concat(" -p").concat(" ").concat(filterValue) : args;
        CommandLineOptions options = new CommandLineOptions().processCommandlineOptions(args.split(WHITE_SPACE_REGEX));
        String filterProject = options.getProject();
        Context context = new Context(options);
        context.setProjectColumn(0);
        prepareColumnOrderMap(context);
        return new LineProcessor(context, filterProject);
    }

    public static Context prepareColumnOrderMap(@NonNull Context context) {
        context.setColumnOrderMap(ImmutableMap.<Integer, String>builder()
                .put(0, DataColumn.PROJECT.getString())
                .put(1, DataColumn.DESCRIPTION.getString())
                .put(2, DataColumn.START_DATE.getString())
                .put(3, DataColumn.CATEGORY.getString())
                .put(4, DataColumn.RESPONSIBLE.getString())
                .put(5, DataColumn.SAVINGS_AMOUNT.getString())
                .put(6, DataColumn.CURRENCY.getString())
                .put(7, DataColumn.COMPLEXITY.getString()).build());
        context.setProjectColumn(0);
        context.setDateColumn(2);
        return context;
    }

    public static ProcessOrchestrator newProcessOrchestrator(String commandLineOptions) throws UserError {
        return new ProcessOrchestrator(new CommandLineOptions().processCommandlineOptions(commandLineOptions.split(WHITE_SPACE_REGEX)));
    }

    public static DataEntry dataEntry(String line) throws UserError {
        String[] args = "-f smalldatafile.txt".split(WHITE_SPACE_REGEX);
        Context context = new Context(new CommandLineOptions().processCommandlineOptions(args));
        prepareColumnOrderMap(context);
        return new DataEntry().fromInputLine(List.of(line.split(TAB)), context.getColumnOrderMap(), 0);
    }

    public static StateInMemory fillMemory(@NonNull List<String> lines, @NonNull Context context) throws UserError {
        StateInMemory stateInMemory = new StateInMemory();
        for (String line : lines) {
            stateInMemory.getDataEntries().add(new DataEntry().fromInputLine(List.of(line.split(TAB_SEPARATOR)), context.getColumnOrderMap(), 0));
        }
        return stateInMemory;
    }
}
