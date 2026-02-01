package labs.lab4.lab42;

import java.util.*;
import java.util.stream.Collectors;

class Student {
    private final String id;
    private final List<Integer> grades;

    public Student(String id, List<Integer> grades) {
        this.id = id;
        this.grades = grades;
    }

    public void addGrade(int grade) {
        grades.add(grade);
    }

    public double avgGrade(){
        return grades.stream().mapToInt(i -> i).average().orElse(0);
    }

    public int numPassed(){
        return (int) grades.stream().mapToInt(i->i).filter(g -> g > 5).count();
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", grades=" + grades +
                '}';
    }
}

class StudentWithIdAlreadyExistsException extends Exception {
    public StudentWithIdAlreadyExistsException(String id) {
        super("Student with ID " + id + " already exists");
    }
}

class Faculty {
    private final Map<String, Student> students = new HashMap<>();

    public void addStudent(String id, List<Integer> grades) throws StudentWithIdAlreadyExistsException {
        if (students.containsKey(id)) {
            throw new StudentWithIdAlreadyExistsException(id);
        } else {
            students.put(id, new Student(id, grades));
        }
    }

    public void addGrade(String id, int grade) {
        Student s = students.get(id);
        s.addGrade(grade);
    }

    public Set<Student> getStudentsSortedByAverageGrade() {
        return students.values().stream()
                .sorted(Comparator.comparing(Student::avgGrade)
                        .thenComparing(Student::numPassed).reversed()
                                .thenComparing(Student::getId)
                )
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Student> getStudentsSortedByCoursesPassed(){
        return students.values().stream()
                .sorted(Comparator.comparing(Student::numPassed)
                        .thenComparing(Student::avgGrade)
                        .thenComparing(Student::getId).reversed()
                )
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}

public class SetsTest {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Faculty faculty = new Faculty();

        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            String[] tokens = input.split("\\s+");
            String command = tokens[0];

            switch (command) {
                case "addStudent":
                    String id = tokens[1];
                    List<Integer> grades = new ArrayList<>();
                    for (int i = 2; i < tokens.length; i++) {
                        grades.add(Integer.parseInt(tokens[i]));
                    }
                    try {
                        faculty.addStudent(id, grades);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case "addGrade":
                    String studentId = tokens[1];
                    int grade = Integer.parseInt(tokens[2]);
                    faculty.addGrade(studentId, grade);
                    break;

                case "getStudentsSortedByAverageGrade":
                    System.out.println("Sorting students by average grade");
                    Set<Student> sortedByAverage = faculty.getStudentsSortedByAverageGrade();
                    for (Student student : sortedByAverage) {
                        System.out.println(student);
                    }
                    break;

                case "getStudentsSortedByCoursesPassed":
                    System.out.println("Sorting students by courses passed");
                    Set<Student> sortedByCourses = faculty.getStudentsSortedByCoursesPassed();
                    for (Student student : sortedByCourses) {
                        System.out.println(student);
                    }
                    break;

                default:
                    break;
            }
        }

        scanner.close();
    }
}
