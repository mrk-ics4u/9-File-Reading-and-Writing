/*
 * TemperatureLogTest.java -- unit tests for the Lesson 9 exercise
 * ICS 4U0 - Lesson 9 Exercise: Temperature Log
 *
 * These tests run TemperatureLog.main() exactly as it will be graded: they
 * type the two file names and the threshold on System.in, check the lines
 * it prints to System.out, and then open the report file it wrote and check
 * that too. You don't need to change this file -- just run the tests (see
 * README.md) and fix TemperatureLog.java until they all pass.
 */
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TemperatureLogTest {

    // JUnit creates an empty folder for each test and deletes it afterward,
    // so the test data files and your report files never clutter the repo.
    @TempDir
    Path tempDir;

    private InputStream originalIn;
    private PrintStream originalOut;
    private Locale originalLocale;

    @BeforeEach
    public void redirectIoAndPinLocale() {
        originalIn = System.in;
        originalOut = System.out;
        originalLocale = Locale.getDefault();
        // printf("%.1f") follows the default locale -- on a machine that uses
        // a comma for decimals, output like "22,6" would fail the exercise
        // for reasons that have nothing to do with your code, so every test
        // pins the locale before calling main().
        Locale.setDefault(Locale.CANADA);
    }

    @AfterEach
    public void restoreIoAndLocale() {
        System.setIn(originalIn);
        System.setOut(originalOut);
        Locale.setDefault(originalLocale);
    }

    /**
     * Feeds {@code input} to TemperatureLog.main() on System.in and returns
     * everything it printed to System.out.
     */
    private String run(String input) throws IOException {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));

        TemperatureLog.main(new String[0]);

        return captured.toString(StandardCharsets.UTF_8);
    }

    /** Writes {@code contents} to a data file in the temp folder and returns its path. */
    private String dataFile(String contents) throws IOException {
        Path path = tempDir.resolve("data.csv");
        Files.writeString(path, contents, StandardCharsets.UTF_8);
        return path.toString();
    }

    /** The path your program should write its report to, in the temp folder. */
    private Path reportPath() {
        return tempDir.resolve("report.csv");
    }

    /** Reads the report file your program wrote, failing clearly if it's missing. */
    private String readReport() throws IOException {
        assertTrue(Files.exists(reportPath()),
                "No report file was created. Did you open a PrintWriter on the report file name you read?");
        String report = Files.readString(reportPath(), StandardCharsets.UTF_8);
        assertTrue(!report.isEmpty(),
                "The report file exists but is empty. Did you close the PrintWriter?");
        return report;
    }

    /**
     * Compares output line by line, trimming trailing whitespace on each
     * line and ignoring one blank line at the very end (println's final
     * newline). Everything else -- including every space, comma, and bracket -- is
     * compared exactly.
     */
    private void assertOutputEquals(String expected, String actual) {
        String[] expectedLines = expected.stripTrailing().split("\n", -1);
        String[] actualLines = actual.stripTrailing().split("\n", -1);

        assertEquals(expectedLines.length, actualLines.length,
                "Expected " + expectedLines.length + " line(s) of output, got " + actualLines.length
                        + ".\n--- expected ---\n" + expected + "--- actual ---\n" + actual);

        for (int i = 0; i < expectedLines.length; i++) {
            assertEquals(expectedLines[i].stripTrailing(), actualLines[i].stripTrailing(),
                    "Line " + (i + 1) + " didn't match.\n--- expected ---\n" + expected
                            + "--- actual ---\n" + actual);
        }
    }

    @Test
    public void exampleFromReadme() throws IOException {
        // Uses the real data file in data/, like the README example.
        String input = "data/september.csv\n" + reportPath() + "\n25\n";
        String expected = "Days read: 30\n"
                + "Average high: 22.6\n"
                + "Warmest day: 2026-09-03 (29.1)\n"
                + "Days above 25.0: 7\n";
        assertOutputEquals(expected, run(input));

        String expectedReport = "date,high\n"
                + "2026-09-01,26.3\n"
                + "2026-09-02,27.8\n"
                + "2026-09-03,29.1\n"
                + "2026-09-04,28.4\n"
                + "2026-09-10,26.6\n"
                + "2026-09-16,25.8\n"
                + "2026-09-17,27.2\n";
        assertOutputEquals(expectedReport, readReport());
    }

    @Test
    public void reportFileIsSavedWhenWriterIsClosed() throws IOException {
        // A PrintWriter that is never closed leaves the report file empty,
        // even though the console output looks right.
        String data = dataFile("date,high\n2026-07-01,31.2\n2026-07-02,24.0\n2026-07-03,28.5\n");
        run(data + "\n" + reportPath() + "\n25\n");

        String expectedReport = "date,high\n"
                + "2026-07-01,31.2\n"
                + "2026-07-03,28.5\n";
        assertOutputEquals(expectedReport, readReport());
    }

    @Test
    public void dayEqualToThresholdIsNotAbove() throws IOException {
        // Using >= instead of > counts and writes the 25.0 day.
        String data = dataFile("date,high\n2026-06-01,25.0\n2026-06-02,26.0\n2026-06-03,22.0\n");
        String expected = "Days read: 3\n"
                + "Average high: 24.3\n"
                + "Warmest day: 2026-06-02 (26.0)\n"
                + "Days above 25.0: 1\n";
        assertOutputEquals(expected, run(data + "\n" + reportPath() + "\n25\n"));

        assertOutputEquals("date,high\n2026-06-02,26.0\n", readReport());
    }

    @Test
    public void noDaysAboveThresholdWritesHeaderOnly() throws IOException {
        // The report still gets its header line when nothing qualifies.
        String data = dataFile("date,high\n2026-07-01,31.2\n2026-07-02,24.0\n2026-07-03,28.5\n");
        String expected = "Days read: 3\n"
                + "Average high: 27.9\n"
                + "Warmest day: 2026-07-01 (31.2)\n"
                + "Days above 35.0: 0\n";
        assertOutputEquals(expected, run(data + "\n" + reportPath() + "\n35\n"));

        assertOutputEquals("date,high\n", readReport());
    }

    @Test
    public void warmestDayStartsFromFirstRowNotZero() throws IOException {
        // Every high is below zero, so a maximum that starts at 0 never
        // finds anything bigger. Start from the first row instead.
        String data = dataFile("date,high\n2026-01-10,-3.5\n2026-01-11,-8.0\n2026-01-12,-1.5\n");
        String expected = "Days read: 3\n"
                + "Average high: -4.3\n"
                + "Warmest day: 2026-01-12 (-1.5)\n"
                + "Days above -5.0: 2\n";
        assertOutputEquals(expected, run(data + "\n" + reportPath() + "\n-5\n"));

        assertOutputEquals("date,high\n2026-01-10,-3.5\n2026-01-12,-1.5\n", readReport());
    }
}
