package SubjectAreaOrg;

import java.io.Serializable;
import java.time.LocalDate;

public class HiringOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int employeeId;
    private String employeeName; // For display
    private LocalDate orderDate;
    private String position;
    private double salary;
    private int probationPeriodMonths;
    private String hrManagerName; // HR Manager who issued the order

    // Default constructor
    public HiringOrder() {
        this.id = 0;
        this.employeeId = 0;
        this.employeeName = "";
        this.orderDate = LocalDate.now();
        this.position = "";
        this.salary = 0.0;
        this.probationPeriodMonths = 0;
        this.hrManagerName = "";
    }

    // Constructor for creating an order
    public HiringOrder(int id, int employeeId, LocalDate orderDate, String position, double salary, int probationPeriodMonths, String hrManagerName) {
        this.id = id;
        this.employeeId = employeeId;
        this.orderDate = orderDate;
        this.position = position;
        this.salary = salary;
        this.probationPeriodMonths = probationPeriodMonths;
        this.hrManagerName = hrManagerName;
        this.employeeName = ""; // Will be set later
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public int getProbationPeriodMonths() { return probationPeriodMonths; }
    public void setProbationPeriodMonths(int probationPeriodMonths) { this.probationPeriodMonths = probationPeriodMonths; }

    public String getHrManagerName() { return hrManagerName; }
    public void setHrManagerName(String hrManagerName) { this.hrManagerName = hrManagerName; }

    @Override
    public String toString() {
        return "HiringOrder{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", orderDate=" + orderDate +
                ", position='" + position + '\'' +
                ", salary=" + salary +
                ", probationPeriodMonths=" + probationPeriodMonths +
                ", hrManagerName='" + hrManagerName + '\'' +
                '}';
    }
}