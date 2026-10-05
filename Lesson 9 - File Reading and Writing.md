# Lesson 9 - File Reading and Writing

Everything a program stores in variables disappears when it ends. A **file** keeps data after the program stops, so the next run (or a different program) can read it back. Real data sets, like the one in your upcoming project, almost always arrive as files: thousands of rows you would never type in by hand.

This lesson reads a text file with the `Scanner` you already know, splits each line into fields, and writes results to a new file.

---

## 1. From Python Files to Java

```python
# Python
with open("scores.csv") as f:
    for line in f:
        print(line.strip())

with open("report.txt", "w") as out:
    out.write("Average: 87.0\n")
```

```java
// Java
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public static void main(String[] args) throws IOException {
    Scanner file = new Scanner(new File("scores.csv"));
    while (file.hasNext()) {
        System.out.println(file.nextLine());
    }
    file.close();

    PrintWriter out = new PrintWriter(new File("report.txt"));
    out.println("Average: 87.0");
    out.close();
}
```

| Python | Java |
|---|---|
| `open(name)` | `new Scanner(new File(name))` |
| `for line in f:` | `while (file.hasNext()) { String line = file.nextLine(); ... }` |
| `open(name, "w")` | `new PrintWriter(new File(name))` |
| `out.write(text + "\n")` | `out.println(text)` |
| `with` closes the file for you | you call `close()` yourself |

`File`, `IOException`, and `PrintWriter` are in `java.io`, so each needs an `import`. A `File` object is just a name for a file on disk. On its own it doesn't open or read anything.

---

## 2. Reading a File with Scanner

A `Scanner` can read from a file instead of the keyboard. Give its constructor a `File` instead of `System.in`:

```java
Scanner file = new Scanner(new File("scores.csv"));
String first = file.nextLine();
```

Every `Scanner` method you've used works the same way on a file. You can have two scanners at once, one on `System.in` for the keyboard and one on the file.

Opening a file can fail: the name might be wrong, or the file might be in a different folder. Java makes you say what should happen in that case. The simplest way is to add `throws IOException` to the header of every method that opens a file, including `main`:

```java
public static void main(String[] args) throws IOException {
```

Without it, the program doesn't compile ("unreported exception FileNotFoundException"). With it, a missing file stops the program with a `FileNotFoundException` that names the file it couldn't find.

**File names are relative to the folder the program runs in.** In VS Code that's the project folder (the one containing `src/`), so `"data/september.csv"` means the `data` folder next to `src`.

---

## 3. Reading Line by Line

`hasNext()` returns `true` while there's something left to read. It's the loop condition for reading a whole file:

```java
Scanner file = new Scanner(new File("scores.csv"));
while (file.hasNext()) {
    String line = file.nextLine();
    // process one line
}
file.close();
```

Call `close()` when you're done reading. It releases the file so other programs (and your own later code) can use it.

In this course we read whole lines with `nextLine()` and convert them with `Integer.parseInt` and `Double.parseDouble`, the same as keyboard input since Lesson 1. Mixing `nextInt()` and `nextLine()` on the same scanner leaves a stray line ending behind and causes bugs that are hard to spot.

---

## 4. Splitting CSV Lines

A **CSV** (comma-separated values) file stores a table as text, one row per line, with commas between the fields. The first line is usually a **header** that names the columns:

```
name,score
Ada,88
Grace,97
```

`split` breaks a `String` into an array around a separator:

```java
String line = "Grace,97";
String[] parts = line.split(",");     // {"Grace", "97"}
String name = parts[0];
int score = Integer.parseInt(parts[1]);
```

Every piece is a `String`, even when it looks like a number, so parse it before doing math.

The header is text, not data. Read it once **before** the loop so the loop only sees data rows:

```java
String header = file.nextLine();
while (file.hasNext()) {
    String[] parts = file.nextLine().split(",");
    ...
}
```

Stick to plain separators like `","` or `" "`. Some characters have a special meaning to `split`: `line.split(".")` returns an empty array.

---

## 5. Writing a File with PrintWriter

`PrintWriter` has the same `print`, `println`, and `printf` methods as `System.out`. The text goes into the file instead of the console:

```java
PrintWriter out = new PrintWriter(new File("report.txt"));
out.println("Report");
out.printf("Average: %.1f%n", average);
out.close();
```

Three things to know:

- **Always `close()` when you're done.** `PrintWriter` collects text in memory and writes it to disk in chunks, and `close()` writes whatever is left. Forget it and you get an empty or cut-off file.
- **Opening a `PrintWriter` erases the file** if it already exists, then starts writing from the top. If the file doesn't exist, it's created.
- To **add to the end** of an existing file instead, open it with `new PrintWriter(new FileWriter("log.txt", true))`. The `true` means append. (`FileWriter` is also in `java.io`.)

---

## 6. Loading a Data Set

Most programs that analyze data do it in two steps: read the whole file into lists, then run algorithms on the lists. Reading everything first means you can loop over the data as many times as you need (to get an average, and then to compare every row with it).

When each row has several fields, use one `ArrayList` per column. Index `i` in each list belongs to row `i` of the file:

```java
ArrayList<String> names = new ArrayList<String>();
ArrayList<Integer> scores = new ArrayList<Integer>();

Scanner file = new Scanner(new File("scores.csv"));
file.nextLine();                              // skip the header
while (file.hasNext()) {
    String[] parts = file.nextLine().split(",");
    names.add(parts[0]);
    scores.add(Integer.parseInt(parts[1]));
}
file.close();

int best = 0;
for (int i = 1; i < scores.size(); i++) {
    if (scores.get(i) > scores.get(best)) {
        best = i;
    }
}
System.out.println(names.get(best) + " (" + scores.get(best) + ")");
```

Every algorithm from Lessons 7 and 8 works on these lists. These are called **parallel lists**, and they get awkward once a row has many fields. Later in the course you'll write a class for one row, and store a single `ArrayList` of those objects instead.

---

## 7. Common File Bugs

**`FileNotFoundException`.** The name is misspelled, or the path is relative to the wrong folder. Check with `new File(name).getAbsolutePath()` to see where Java is actually looking.

**Missing `throws IOException`.** "Unreported exception" is a compile error. Add it to every method that opens a file.

**Not skipping the header.** `Integer.parseInt("score")` throws `NumberFormatException` on the first row.

**Forgetting `close()` on a `PrintWriter`.** The output file exists but is empty.

**`new Scanner("scores.csv")`.** Without `new File(...)`, the scanner reads the text `scores.csv` itself, not the file.

**Parsing before splitting.** `Integer.parseInt("Grace,97")` fails. Split first, then parse one piece.

---

## Try It Yourself

Compile and run the companion file in this folder:

```bash
javac FileIODemo.java
java FileIODemo
```

The demo creates its own small files, prints what it reads and writes, and deletes them at the end.

Then work on the exercise in `TemperatureLog.java`.

---

## Course Expectations

### ICS 4U

"I can read from, and write to an external file"
is a requirement of the course and will be expected in your programming project.
### AP Expectations

The following are expectations of the AP exam and will show up on the final exam:

- A file stores data that persists when the program is not running, and can be read while the program runs (Topic 4.6, Using Text Files, 4.6.A.1)
- A file is connected to a program with the `File` and `Scanner` classes; `File(String str)` opens the file named by `str` (4.6.A.2, 4.6.A.3)
- A method that uses `File` must say what to do if the file can't be opened, for example by adding `throws IOException` to its header; an invalid name then ends the program (4.6.A.4)
- `File` and `IOException` are in `java.io` and need an `import` (4.6.A.5)
- The Quick Reference `Scanner` members: `Scanner(File f)`, `nextInt()`, `nextDouble()`, `nextBoolean()`, `nextLine()`, `next()`, `hasNext()`, and `close()`, including what each returns and that `nextInt`, `nextDouble`, and `nextBoolean` throw `InputMismatchException` when the next item isn't the right type (4.6.A.6)
- `nextLine()` can return the empty string right after another `Scanner` method, because the methods handle whitespace differently; writing code that mixes them is excluded from the exam (4.6.A.7)
- `String[] split(String del)` returns the pieces of a `String` around `del`; regular-expression special characters in `del` are excluded from the exam (4.6.A.8)
- A `while` loop with `hasNext()` as its condition reads until the file runs out (4.6.A.9)
- A file should be closed with `close()` when the program is done with it (4.6.A.10)

Keyboard input is excluded from the AP exam (4.6.A.6). On the exam, `Scanner` always reads from a file.
