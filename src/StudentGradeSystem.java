import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * CodeAlpha Task: Student Grade System
 * Console-based program to input and manage student grades.
 */
public class StudentGradeSystem {

    private static final List<Student> students = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("      STUDENT GRADE SYSTEM");
        System.out.println("=====================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    addGradeToStudent();
                    break;
                case 3:
                    viewStudentDetails();
                    break;
                case 4:
                    displaySummaryReport();
                    break;
                case 5:
                    removeStudent();
                    break;
                case 6:
                    running = false;
                    System.out.println("Thank you for using the Student Grade System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 6.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n---------------- MENU ----------------");
        System.out.println("1. Add a new student (with grades)");
        System.out.println("2. Add a grade to an existing student");
        System.out.println("3. View one student's details");
        System.out.println("4. Display summary report of all students");
        System.out.println("5. Remove a student");
        System.out.println("6. Exit");
        System.out.println("--------------------------------------");
    }

    // ---------- Menu actions ----------

    private static void addStudent() {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        if (findStudent(name) != null) {
            System.out.println("A student with this name already exists. Use option 2 to add grades.");
            return;
        }

        Student student = new Student(name);
        int count = readInt("How many grades do you want to enter for " + name + "? ");
        if (count < 0) {
            System.out.println("Number of grades cannot be negative.");
            return;
        }
        for (int i = 1; i <= count; i++) {
            double grade = readGrade("  Grade " + i + " (0-100): ");
            student.addGrade(grade);
        }
        students.add(student);
        System.out.println("Student '" + name + "' added successfully.");
    }

    private static void addGradeToStudent() {
        if (students.isEmpty()) {
            System.out.println("No students available. Add a student first.");
            return;
        }
        System.out.print("Enter student name: ");
        Student student = findStudent(scanner.nextLine().trim());
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        double grade = readGrade("Enter grade (0-100): ");
        student.addGrade(grade);
        System.out.println("Grade added for " + student.getName() + ".");
    }

    private static void viewStudentDetails() {
        if (students.isEmpty()) {
            System.out.println("No students available.");
            return;
        }
        System.out.print("Enter student name: ");
        Student student = findStudent(scanner.nextLine().trim());
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        System.out.println("\nName    : " + student.getName());
        System.out.println("Grades  : " + student.getGrades());
        if (student.getGrades().isEmpty()) {
            System.out.println("No grades recorded yet.");
            return;
        }
        System.out.printf("Average : %.2f%n", student.getAverage());
        System.out.printf("Highest : %.2f%n", student.getHighest());
        System.out.printf("Lowest  : %.2f%n", student.getLowest());
        System.out.println("Grade   : " + student.getLetterGrade());
    }

    private static void removeStudent() {
        if (students.isEmpty()) {
            System.out.println("No students available.");
            return;
        }
        System.out.print("Enter student name to remove: ");
        Student student = findStudent(scanner.nextLine().trim());
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        students.remove(student);
        System.out.println("Student '" + student.getName() + "' removed.");
    }

    /** Prints a table of all students plus class-wide statistics. */
    private static void displaySummaryReport() {
        if (students.isEmpty()) {
            System.out.println("No students available. Add a student first.");
            return;
        }

        System.out.println("\n==================== SUMMARY REPORT ====================");
        System.out.printf("%-20s %8s %8s %8s %6s%n", "Name", "Average", "Highest", "Lowest", "Grade");
        System.out.println("--------------------------------------------------------");

        double classTotal = 0;
        int gradedStudents = 0;
        double classHighest = Double.NEGATIVE_INFINITY;
        double classLowest = Double.POSITIVE_INFINITY;
        String topStudent = "-";
        String lowStudent = "-";

        for (Student s : students) {
            if (s.getGrades().isEmpty()) {
                System.out.printf("%-20s %8s %8s %8s %6s%n", s.getName(), "N/A", "N/A", "N/A", "N/A");
                continue;
            }
            System.out.printf("%-20s %8.2f %8.2f %8.2f %6s%n",
                    s.getName(), s.getAverage(), s.getHighest(), s.getLowest(), s.getLetterGrade());

            classTotal += s.getAverage();
            gradedStudents++;
            if (s.getHighest() > classHighest) {
                classHighest = s.getHighest();
                topStudent = s.getName();
            }
            if (s.getLowest() < classLowest) {
                classLowest = s.getLowest();
                lowStudent = s.getName();
            }
        }

        System.out.println("--------------------------------------------------------");
        System.out.println("Total students : " + students.size());
        if (gradedStudents > 0) {
            System.out.printf("Class average  : %.2f%n", classTotal / gradedStudents);
            System.out.printf("Highest score  : %.2f (%s)%n", classHighest, topStudent);
            System.out.printf("Lowest score   : %.2f (%s)%n", classLowest, lowStudent);
        } else {
            System.out.println("No grades have been entered yet.");
        }
        System.out.println("========================================================");
    }

    // ---------- Helpers ----------

    private static Student findStudent(String name) {
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readGrade(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value < 0 || value > 100) {
                    System.out.println("Grade must be between 0 and 100.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
