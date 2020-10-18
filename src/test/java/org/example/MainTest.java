package org.example;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private static final String OPTIONS_ERROR = "USER ERROR -- Invalid options. Use -f or --file option to supply file followed by file name. Use -p or --project followed by project name to filter on project.\n";

    @BeforeAll
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterAll
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("should not throw error")
    void should_not_throw_error() {
        String[] args1 = "-p 1 -s -f smalldatafile.txt".split(TestUtil.WHITE_SPACE_REGEX);
        String[] args2 = "-s -f smalldatafile.txt".split(TestUtil.WHITE_SPACE_REGEX);
        String[] args3 = "-p 1 -f smalldatafile.txt".split(TestUtil.WHITE_SPACE_REGEX);
        String[] args4 = "-f smalldatafile.txt".split(TestUtil.WHITE_SPACE_REGEX);
        List<String[]> scenarios = List.of(args1, args2, args3, args4);

        scenarios.forEach(scenario -> {
            new Main().runProcess(args1);
            assertNotEquals(outContent.toString(), OPTIONS_ERROR);
            outContent.reset();
        });
    }

    @Test
    @DisplayName("should throw error")
    void should_throw_error() {
        String[] args1 = "-p 1 -s -f".split(TestUtil.WHITE_SPACE_REGEX);
        String[] args2 = "-s smalldatafile.txt".split(TestUtil.WHITE_SPACE_REGEX);
        String[] args3 = "-s".split(TestUtil.WHITE_SPACE_REGEX);
        String[] args4 = "".split(TestUtil.WHITE_SPACE_REGEX);
        List<String[]> scenarios = List.of(args1, args2, args3, args4);

        scenarios.forEach(scenario -> {
            new Main().runProcess(args1);
            assertEquals(outContent.toString(), OPTIONS_ERROR);
            outContent.reset();
        });
    }

}