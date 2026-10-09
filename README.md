# CodeAlpha_StudentGradeSystem

A console-based Java program to input and manage student grades. Built as a task for the **CodeAlpha Java Programming Internship**.

## Features

- Add students with any number of grades (stored in `ArrayList`s)
- Add more grades to an existing student later
- Per-student **average, highest and lowest** score, plus a letter grade
- **Summary report** of all students in a table, with class average, top score and lowest score
- Remove a student
- Input validation (grades must be numbers from 0 to 100; menu input is checked)

## Project structure

```
CodeAlpha_StudentGradeSystem/
├── src/
│   ├── Student.java              # Student data + average/highest/lowest logic
│   └── StudentGradeSystem.java   # Console menu (main class)
├── .gitignore
└── README.md
```

## How to run

Requires JDK 8 or later.

```bash
javac -d out src/*.java
java -cp out StudentGradeSystem
```

## Sample output

```
==================== SUMMARY REPORT ====================
Name                  Average  Highest   Lowest  Grade
--------------------------------------------------------
Alice                   89.17    92.00    85.50      B
Bob                     52.50    60.00    45.00      F
Cara                    77.00    77.00    77.00      C
--------------------------------------------------------
Total students : 3
Class average  : 72.89
Highest score  : 92.00 (Alice)
Lowest score   : 45.00 (Bob)
========================================================
```

## Grading scale

| Average | Grade |
|---------|-------|
| 90 and above | A |
| 80 - 89.99 | B |
| 70 - 79.99 | C |
| 60 - 69.99 | D |
| below 60 | F |

## Concepts used

Object-oriented programming (classes, encapsulation), `ArrayList`, loops, `switch`, exception handling (`NumberFormatException`), formatted output with `printf`.

## Author

John Britto M
