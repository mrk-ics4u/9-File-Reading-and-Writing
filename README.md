# ICS 4U0 — Lesson 9: File Reading and Writing

## Exercise

You're building a temperature log that reads a month of daily highs from a CSV file, reports on them, and saves the hot days to a new CSV file.

Everything you need is from Lesson 9:

- **reading a file** with `File` and `Scanner`, and a `hasNext()` loop,
- **skipping the header** and **splitting** each line on `","`,
- **loading a data set** into two `ArrayList`s, and
- **writing a file** with `PrintWriter`, and closing it.

- Read three lines from the keyboard: the name of the data file, the name of the report file to write, and a threshold temperature.
- The data file starts with the header line `date,high`, then one `date,high` row per day. A sample with 30 days is in `data/september.csv`.
- "Warmest day" is the day with the highest high. On a tie, report the earliest day.
- Write the report file: the same header line `date,high`, then every day whose high is **strictly greater** than the threshold, in the same order and the same `date,high` format as the data file. If no day qualifies, the report is just the header line.
- Print the four console lines exactly as shown below.

---

## Input

Three lines from the keyboard:

| Line | Value | Type |
|------|-------|------|
| 1 | Name of the data file to read | text |
| 2 | Name of the report file to write | text |
| 3 | Threshold temperature | decimal number |

Example input:

```
data/september.csv
hot-days.csv
25
```

The data file (first rows of `data/september.csv`):

```
date,high
2026-09-01,26.3
2026-09-02,27.8
2026-09-03,29.1
...
```

---

## Output

Exactly four lines on the console:

```
Days read: <number of data rows>
Average high: <average to 1 decimal>
Warmest day: <date> (<high to 1 decimal>)
Days above <threshold to 1 decimal>: <number of days>
```

For the example input above, your program must print **exactly**:

```
Days read: 30
Average high: 22.6
Warmest day: 2026-09-03 (29.1)
Days above 25.0: 7
```

and `hot-days.csv` must contain **exactly**:

```
date,high
2026-09-01,26.3
2026-09-02,27.8
2026-09-03,29.1
2026-09-04,28.4
2026-09-10,26.6
2026-09-16,25.8
2026-09-17,27.2
```

Every space, comma, and bracket is compared, in both the console output and the report file. `Warmest day: 2026-09-03 (29.1)` and `Warmest day: 2026-09-03(29.1)` are not the same answer.

---

## Testing

- Test your code yourself first.
- Open the **Testing** panel from the sidebar (flask icon) and click ▶ **Run Tests**.

A green check next to a test means it passed; a red X means it failed and will show you the expected vs. actual output.
