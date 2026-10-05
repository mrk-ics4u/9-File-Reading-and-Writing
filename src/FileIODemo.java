/*
 * Name:        Lesson 9: File Reading and Writing (FileIODemo.java)
 * Description: A runnable tour of reading a text file with File and Scanner,
 *              splitting CSV lines, writing a file with PrintWriter, and
 *              loading a small data set into ArrayLists.
 * Created by:  Mr Kowalczewski
 * Last edited: 2026-09-24
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class FileIODemo {

    // the demo makes its own files so it runs from any folder, and deletes them at the end
    public static final String SCORES_FILE = "demo-scores.csv";
    public static final String REPORT_FILE = "demo-report.txt";

    public static void main(String[] args) throws IOException {
        makeSampleFile();

        pythonFilesToJava();
        readingAFile();
        readingLineByLine();
        splittingCsvLines();
        writingAFile();
        loadingADataSet();
        commonFileBugs();

        new File(SCORES_FILE).delete();
        new File(REPORT_FILE).delete();
    }

    // method to create the small CSV file the other sections read
    public static void makeSampleFile() throws IOException {
        PrintWriter out = new PrintWriter(new File(SCORES_FILE));
        out.println("name,score");
        out.println("Ada,88");
        out.println("Grace,97");
        out.println("Alan,72");
        out.println("Linus,91");
        out.close();
    }

    // method to demonstrate that a file outlives the program
    public static void pythonFilesToJava() {
        System.out.println();
        System.out.println("=== 1. from python files to java ===");

        File f = new File(SCORES_FILE);
        System.out.println("file name:     " + f.getName());
        System.out.println("full path:     " + f.getAbsolutePath());
        System.out.println("exists():      " + f.exists());
        System.out.println("length():      " + f.length() + " bytes");
    }

    // method to demonstrate File + Scanner and throws IOException
    public static void readingAFile() throws IOException {
        System.out.println();
        System.out.println("=== 2. reading a file with scanner ===");

        Scanner file = new Scanner(new File(SCORES_FILE));
        String first = file.nextLine();    // same nextLine() as keyboard input
        String second = file.nextLine();
        System.out.println("line 1: " + first);
        System.out.println("line 2: " + second);
        file.close();
    }

    // method to demonstrate the hasNext() loop
    public static void readingLineByLine() throws IOException {
        System.out.println();
        System.out.println("=== 3. reading line by line ===");

        Scanner file = new Scanner(new File(SCORES_FILE));
        int lineNumber = 0;
        while (file.hasNext()) {
            String line = file.nextLine();
            lineNumber++;
            System.out.println(lineNumber + ": " + line);
        }
        file.close();
        System.out.println("hasNext() is now false after " + lineNumber + " lines");
    }

    // method to demonstrate split(",") and parsing the pieces
    public static void splittingCsvLines() {
        System.out.println();
        System.out.println("=== 4. splitting csv lines ===");

        String line = "Grace,97";
        String[] parts = line.split(",");
        System.out.println("parts.length = " + parts.length);
        System.out.println("parts[0] = " + parts[0] + ", parts[1] = " + parts[1]);

        int score = Integer.parseInt(parts[1]);   // every piece is a String until you parse it
        System.out.println("score + 1 = " + (score + 1));

        // "." is a special character in split, so this returns an empty array
        String date = "2026.09.24";
        System.out.println("\"2026.09.24\".split(\".\").length = " + date.split(".").length);
    }

    // method to demonstrate PrintWriter, close, overwriting, and appending
    public static void writingAFile() throws IOException {
        System.out.println();
        System.out.println("=== 5. writing a file with printwriter ===");

        PrintWriter out = new PrintWriter(new File(REPORT_FILE));
        out.println("Report");
        out.printf("Average: %.1f%n", 87.0);
        out.close();                                   // nothing is saved until close()
        printFile(REPORT_FILE);

        // opening a PrintWriter on an existing file ERASES it first
        out = new PrintWriter(new File(REPORT_FILE));
        out.println("Replaced");
        out.close();
        printFile(REPORT_FILE);

        // new FileWriter(name, true) opens the file to ADD to the end
        out = new PrintWriter(new FileWriter(REPORT_FILE, true));
        out.println("Appended");
        out.close();
        printFile(REPORT_FILE);
    }

    // method to print a file's contents with a label
    public static void printFile(String name) throws IOException {
        Scanner file = new Scanner(new File(name));
        String contents = "";
        while (file.hasNext()) {
            contents += file.nextLine() + " | ";
        }
        file.close();
        System.out.println(name + ": " + contents);
    }

    // method to demonstrate reading a whole data set into parallel ArrayLists
    public static void loadingADataSet() throws IOException {
        System.out.println();
        System.out.println("=== 6. loading a data set ===");

        ArrayList<String> names = new ArrayList<String>();
        ArrayList<Integer> scores = new ArrayList<Integer>();

        Scanner file = new Scanner(new File(SCORES_FILE));
        String header = file.nextLine();              // read the header once, before the loop
        while (file.hasNext()) {
            String[] parts = file.nextLine().split(",");
            names.add(parts[0]);
            scores.add(Integer.parseInt(parts[1]));
        }
        file.close();

        System.out.println("header: " + header);
        System.out.println("names:  " + names);
        System.out.println("scores: " + scores);

        // index i in one list matches index i in the other
        int best = 0;
        for (int i = 1; i < scores.size(); i++) {
            if (scores.get(i) > scores.get(best)) {
                best = i;
            }
        }
        System.out.println("best: " + names.get(best) + " (" + scores.get(best) + ")");
    }

    // method to demonstrate common file mistakes
    public static void commonFileBugs() {
        System.out.println();
        System.out.println("=== 7. common file bugs ===");

        // a wrong name or wrong folder: FileNotFoundException (try/catch just keeps the demo running)
        try {
            Scanner file = new Scanner(new File("no-such-file.csv"));
            file.close();
        } catch (FileNotFoundException e) {
            System.out.println("missing file -> " + e.getMessage());
        }

        // forgetting to skip the header: parsing "score" as a number
        try {
            Integer.parseInt("score");
        } catch (NumberFormatException e) {
            System.out.println("header not skipped -> " + e.getMessage());
        }

        // new Scanner("demo-scores.csv") scans the NAME, not the file
        Scanner wrong = new Scanner(SCORES_FILE);
        System.out.println("new Scanner(name).nextLine() = " + wrong.nextLine());
        wrong.close();
    }
}
