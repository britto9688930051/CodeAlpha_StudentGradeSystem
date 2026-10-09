import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student and the grades (out of 100) they have received.
 */
public class Student {
    private final String name;
    private final List<Double> grades = new ArrayList<>();

    public Student(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Double> getGrades() {
        return grades;
    }

    public void addGrade(double grade) {
        grades.add(grade);
    }

    public double getAverage() {
        if (grades.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (double g : grades) {
            sum += g;
        }
        return sum / grades.size();
    }

    public double getHighest() {
        if (grades.isEmpty()) {
            return 0;
        }
        double max = grades.get(0);
        for (double g : grades) {
            if (g > max) {
                max = g;
            }
        }
        return max;
    }

    public double getLowest() {
        if (grades.isEmpty()) {
            return 0;
        }
        double min = grades.get(0);
        for (double g : grades) {
            if (g < min) {
                min = g;
            }
        }
        return min;
    }

    /** Converts the student's average into a letter grade. */
    public String getLetterGrade() {
        double avg = getAverage();
        if (avg >= 90) return "A";
        if (avg >= 80) return "B";
        if (avg >= 70) return "C";
        if (avg >= 60) return "D";
        return "F";
    }
}
