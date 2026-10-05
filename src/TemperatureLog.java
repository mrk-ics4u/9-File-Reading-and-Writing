/*
 * Name:
 * Description:
 * Created by:
 * Last edited:
 */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class TemperatureLog {

    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);

        // read the data file name, the report file name, and the threshold (double) from the keyboard
        String dataFileName = input.nextLine().trim();


        // open the data file with a second Scanner, skip the header line,
        // and add each date and high to two ArrayLists (split each line on ",")


        // close the data file


        // count, average, and warmest day (the first one on a tie)


        // open a PrintWriter on the report file, write the header line,
        // then every day with a high strictly above the threshold, in the same "date,high" format


        // close the report file (nothing is saved until you do)


        // print the four console lines (README.md)


    }
}
