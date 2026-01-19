package e29;

import com.sun.source.tree.Tree;

import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

class BonusNotAllowedException extends Exception {
    public BonusNotAllowedException(String message) {
        super(message);
    }
}

enum TYPE {
    HourlyEmployee,
    FreelanceEmployee
}

interface IEmployee {
    double getSalary(Map<String, Double> rateByLevel);

    TYPE getType();

    double overtimeSalary();

    int ticketPointsSum();
}

abstract class Employee implements IEmployee {
    protected String id;
    protected String level;
    protected double salary;
    protected Bonus bonus;

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

    public void setBonus(Bonus bonus) {
        this.bonus = bonus;
    }

    public double getBonusValue() {
        return bonus.getBonus();
    }
}

class HourlyEmployee extends Employee {
    private double hours;
    private static double STANDARD_HOURS = 40;
    private double calculatedOvertime = 0;


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
    public double overtimeSalary() {
        return calculatedOvertime;
    }

    @Override
    public int ticketPointsSum() {
        return 0;
    }

    @Override
    public double getSalary(Map<String, Double> rateByLevel) {
//        ex. level1 50$
        double rate = rateByLevel.get(level);
        this.calculatedOvertime = overtimeHours() * rate * 1.5;
        return regularHours() * rate + overtimeHours() * rate * 1.5;
    }

    @Override
    public TYPE getType() {
        return TYPE.HourlyEmployee;
    }

    @Override
    public String toString() {
//        Employee ID: 157f3d Level: level10 Salary: 2390.72 Regular hours: 40.00 Overtime hours: 23.14
        return String.format("Employee ID: %s Level: %s Salary: %.2f Regular hours: %.2f Overtime hours: %.2f Bonus: %.2f"
                , id, level, salary, regularHours(), overtimeHours(), getBonusValue());
    }
}

class FreelanceEmployee extends Employee {
    private List<Integer> ticketPoints;

    public FreelanceEmployee(String id, String level, List<Integer> ticketPoints) {
        super(id, level);
        this.ticketPoints = ticketPoints;
    }

    @Override
    public int ticketPointsSum() {
        return ticketPoints.stream()
                .mapToInt(Integer::intValue)
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
    public double overtimeSalary() {
        return 0;
    }

    @Override
    public String toString() {
//        Employee ID: 596ed2 Level: level10 Salary: 1290.00 Tickets count: 9 Tickets points: 43
        return String.format("Employee ID: %s Level: %s Salary: %.2f Tickets count: %d Tickets points: %d"
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
                for (int i = 3; i < parts.length - 1; i++) {
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

    public double getSalaryForEmployee(Employee employee) {
        if (employee.getType() == TYPE.HourlyEmployee) {
            return employee.getSalary(hourlyRateByLevel);
        } else {
            return employee.getSalary(ticketRateByLevel);
        }
    }

    public void calculateSalaries() {
        employeeList.forEach(employee -> {
            if (employee.getType() == TYPE.HourlyEmployee)
                employee.setSalary(employee.getSalary(hourlyRateByLevel));
            else
                employee.setSalary(employee.getSalary(ticketRateByLevel));
        });
    }

    public Map<String, Set<Employee>> printEmployeesByLevels(OutputStream os, Set<String> levels) {
        //  os is not used because of the print that is present in the main
        return employeeList.stream()
                .filter(e -> levels.contains(e.getLevel()))
                .sorted(Comparator.comparingDouble(this::getSalaryForEmployee)
                        .reversed()
                        .thenComparing(Employee::getLevel))
                .collect(Collectors.groupingBy(
                        Employee::getLevel,
                        TreeMap::new,
                        Collectors.toCollection(LinkedHashSet::new)
                ));
    }


    public Employee createEmployee(String line) throws BonusNotAllowedException {
        String[] parts = line.split(";|\\s+");
        Employee employee = EmployeeReader.read(parts);

        String bonusString = parts[parts.length - 1];
        double bonusValue = Double.parseDouble(bonusString.replace("%", ""));
        if (bonusString.contains("%")) {
            if (bonusValue > 20) throw new BonusNotAllowedException("Bonus not allowed");
            employee.setBonus(new PercentageBonus(employee, bonusValue));
        } else {
            if (bonusValue > 1000) throw new BonusNotAllowedException("Bonus not allowed");
            employee.setBonus(new FixedBonus(bonusValue));
        }
        employeeList.add(employee);
        return employee;
    }

    public Map<String, Double> getOvertimeSalaryForLevels() {
        // <level, totalOvertimeSalary>
        return employeeList.stream()
                .collect(Collectors.toMap(
                        Employee::getLevel,
                        Employee::overtimeSalary,
                        Double::sum
                ));
    }

    public void printStatisticsForOvertimeSalary() {
        double min = employeeList.stream().mapToDouble(Employee::overtimeSalary)
                .min()
                .orElse(0);
        double max = employeeList.stream().mapToDouble(Employee::overtimeSalary)
                .max()
                .orElse(0);
        double sum = employeeList.stream().mapToDouble(Employee::overtimeSalary)
                .sum();

        double average = employeeList.stream().mapToDouble(Employee::overtimeSalary)
                .average()
                .orElse(0);
//        Statistics for overtime salary: Min: 285.98 Average: 774.49 Max: 1250.04 Sum: 3097.94
        System.out.printf("Statistics for overtime salary: Min: %.2f Average: %.2f Max: %.2f Sum: %.2f", min, average, max, sum);
    }

    public Map<String, Integer> ticketsDoneByLevel() {
        // <level, ticketPoints>
        return employeeList.stream()
                .collect(Collectors.toMap(
                        Employee::getLevel,
                        Employee::ticketPointsSum,
                        Integer::sum
                ));
    }

    public Collection<Employee> getFirstNEmployeesByBonus(int n) {
        return employeeList
                .stream()
                .sorted(Comparator.comparingDouble(Employee::getBonusValue).reversed())
                .limit(n)
                .collect(Collectors.toCollection(LinkedList::new));
    }

}

interface Bonus {
    double getBonus();
}

class FixedBonus implements Bonus {
    private double amount;

    public FixedBonus(double amount) {
        this.amount = amount;
    }

    @Override
    public double getBonus() {
        return amount;
    }
}

class PercentageBonus implements Bonus {
    private Employee employee;
    private double percentage;

    public PercentageBonus(Employee employee, double value) {
        this.employee = employee;
        this.percentage = value / 100;
    }

    @Override
    public double getBonus() {
        if (employee.salary == 0) return 0;
        return employee.salary * percentage;
    }
}

public class PayrollSystemTest2 {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 11 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5.5 + i * 2.5);
        }

        Scanner sc = new Scanner(System.in);

        int employeesCount = Integer.parseInt(sc.nextLine());

        PayrollSystem ps = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);
        Employee emp = null;
        for (int i = 0; i < employeesCount; i++) {
            try {
                emp = ps.createEmployee(sc.nextLine());
            } catch (BonusNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        }
        ps.calculateSalaries();
        int testCase = Integer.parseInt(sc.nextLine());

        switch (testCase) {
            case 1: //Testing createEmployee
                if (emp != null)
                    System.out.println(emp);
                break;
            case 2: //Testing getOvertimeSalaryForLevels()
                ps.getOvertimeSalaryForLevels().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Overtime salary: %.2f\n", level, overtimeSalary);
                });
                break;
            case 3: //Testing printStatisticsForOvertimeSalary()
                ps.printStatisticsForOvertimeSalary();
                break;
            case 4: //Testing ticketsDoneByLevel
                ps.ticketsDoneByLevel().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Tickets by level: %d\n", level, overtimeSalary);
                });
                break;
            case 5: //Testing getFirstNEmployeesByBonus (int n)
                ps.getFirstNEmployeesByBonus(Integer.parseInt(sc.nextLine())).forEach(System.out::println);
                break;
        }

    }
}