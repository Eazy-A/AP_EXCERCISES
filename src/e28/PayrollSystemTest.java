package e28;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

enum TYPE {
    HourlyEmployee,
    FreelanceEmployee
}

interface IEmployee {
    double getSalary(Map<String, Double> rateByLevel);

    TYPE getType();
}

abstract class Employee implements IEmployee {
    protected String id;
    protected String level;
    protected double salary;

    public Employee(String id, String level) {
        this.id = id;
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public String getLevel() {
        return level;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}

class HourlyEmployee extends Employee {
    private double hours;
    private static double STANDARD_HOURS = 40;

    public HourlyEmployee(String id, String level, double hours) {
        super(id, level);
        this.hours = hours;
    }

    public double regularHours() {
        if (hours <= STANDARD_HOURS) return hours;
        return STANDARD_HOURS;
    }

    public double overtimeHours() {
        if (hours <= STANDARD_HOURS) return 0;
        return hours - STANDARD_HOURS;
    }

    @Override
    public double getSalary(Map<String, Double> rateByLevel) {
//        ex. level1 50$
        double rate = rateByLevel.get(level);
        return regularHours() * rate + overtimeHours() * rate * 1.5;
    }

    @Override
    public TYPE getType() {
        return TYPE.HourlyEmployee;
    }

    @Override
    public String toString() {
//        Employee ID: 157f3d Level: level10 Salary: 2390.72 Regular hours: 40.00 Overtime hours: 23.14
        return String.format("Employee ID: %s Level: %s Salary: %.2f Regular hours: %.2f Overtime hours: %.2f"
                , id, level, salary, regularHours(), overtimeHours());
    }
}

class FreelanceEmployee extends Employee {
    private List<Integer> ticketPoints;

    public FreelanceEmployee(String id, String level, List<Integer> ticketPoints) {
        super(id, level);
        this.ticketPoints = ticketPoints;
    }


    public double ticketPointsSum() {
        return ticketPoints.stream()
                .mapToDouble(Double::valueOf)
                .sum();
    }

    @Override
    public double getSalary(Map<String, Double> rateByLevel) {
        double rate = rateByLevel.get(level);
        return rate * ticketPointsSum();

    }

    @Override
    public TYPE getType() {
        return TYPE.FreelanceEmployee;
    }

    @Override
    public String toString() {
//        Employee ID: 596ed2 Level: level10 Salary: 1290.00 Tickets count: 9 Tickets points: 43
        return String.format("Employee ID: %s Level: %s Salary: %.2f Tickets count: %d Tickets points: %.0f"
                , id, level, salary, ticketPoints.size(), ticketPointsSum());
    }
}

class EmployeeReader {
    public static Employee read(String[] parts) {
        String type = parts[0];
        String id = parts[1];
        String level = parts[2];

        switch (type) {
            case "H":
                double hours = Double.parseDouble(parts[3]);
                return new HourlyEmployee(id, level, hours);
            case "F":
                List<Integer> ticketPoints = new ArrayList<>();
                for (int i = 3; i < parts.length; i++) {
                    ticketPoints.add(Integer.parseInt(parts[i]));
                }
                return new FreelanceEmployee(id, level, ticketPoints);
            default:
                throw new IllegalStateException("Unexpected value");
        }
    }

}

class PayrollSystem {

    private Set<Employee> employeeList = new HashSet<>();
    private Map<String, Double> hourlyRateByLevel;
    private Map<String, Double> ticketRateByLevel;

    public PayrollSystem(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
        this.hourlyRateByLevel = hourlyRateByLevel;
        this.ticketRateByLevel = ticketRateByLevel;
    }

    public void readEmployeesData(InputStream is) {
//    HourlyEmployee: H;ID;level;hours;
//    FreelanceEmployee: F;ID;level;ticketPoints1;ticketPoints2;...;ticketPointsN;
        Scanner scanner = new Scanner(is);
        while (scanner.hasNextLine()) {
            String[] parts = scanner.nextLine().split(";");
            employeeList.add(EmployeeReader.read(parts));
        }
    }

    public double getSalaryForEmployee(Employee employee) {
        if (employee.getType() == TYPE.HourlyEmployee) {
            return employee.getSalary(hourlyRateByLevel);
        } else {
            return employee.getSalary(ticketRateByLevel);
        }
    }

    public void calculateSalaries(){
        employeeList.forEach(employee -> {
            if(employee.getType() == TYPE.HourlyEmployee)
                employee.setSalary(employee.getSalary(hourlyRateByLevel));
            else
                employee.setSalary(employee.getSalary(ticketRateByLevel));
        });
    }

    public Map<String, Set<Employee>> printEmployeesByLevels(OutputStream os, Set<String> levels) {
        Map<String, Set<Employee>> groupedSorted = employeeList.stream()
                .filter(e -> levels.contains(e.getLevel()))
                .sorted(Comparator.comparingDouble(this::getSalaryForEmployee)
                        .reversed()
                        .thenComparing(Employee::getLevel))
                .collect(Collectors.groupingBy(
                        Employee::getLevel,
                        TreeMap::new,
                        Collectors.toCollection(LinkedHashSet::new)
                ));
//        Map<String, List<Employee>> groupedSorted = employeeList.stream()
//                .filter(e -> levels.contains(e.getLevel()))
//                .collect(Collectors.groupingBy(
//                        Employee::getLevel,
//                        Collectors.collectingAndThen(
//                                Collectors.t(),
//                                list -> {
//                                    list.sort(Comparator.comparing(this::getSalaryForEmployee).reversed()
//                                            .thenComparing(Employee::getLevel));
//                                    return list;
//                                }
//                        )
//                ));

//        PrintWriter pw = new PrintWriter(os);
//        groupedSorted.forEach((level, list) -> {
//            pw.println("Level " + level + ":");
//            list.forEach(e -> pw.println(" " + e));
//        });
//        pw.flush();
        //  os is not used because of the print that is present in the main
        return groupedSorted;
    }

}

public class PayrollSystemTest {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployeesData(System.in);
        payrollSystem.calculateSalaries();
        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        for (int i = 5; i <= 10; i++) {
            levels.add("level" + i);
        }
        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(System.out, levels);
        result.forEach((level, employees) -> {
            System.out.println("LEVEL: " + level);
            System.out.println("Employees: ");
            employees.forEach(System.out::println);
            System.out.println("------------");
        });


    }
}