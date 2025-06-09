// Example/Client/src/main/java/SubjectAreaOrg/Vacation.java
// Example/Server/src/main/java/SubjectAreaOrg/Vacation.java
package SubjectAreaOrg;

import java.io.Serializable;
import java.time.LocalDate;

public class Vacation implements Serializable {
    private static final long serialVersionUID = 1L; // Для сериализации

    private int id;
    private int employeeId;
    private String employeeName; // Для отображения в таблице (Имя Фамилия)
    private LocalDate startDate;
    private LocalDate endDate;
    private int year;
    private String status;

    public Vacation() {
        this.id = 0;
        this.employeeId = 0;
        this.employeeName = "";
        this.startDate = null;
        this.endDate = null;
        this.year = LocalDate.now().getYear();
        this.status = "Запланирован";
    }

    public Vacation(int id, int employeeId, LocalDate startDate, LocalDate endDate, int year, String status) {
        this.id = id;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.year = year;
        this.status = status;
        this.employeeName = ""; // Будет установлено позже или через конструктор
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Vacation{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", year=" + year +
                ", status='" + status + '\'' +
                '}';
    }
}