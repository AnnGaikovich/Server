package DB;

import SubjectAreaOrg.Employee;
import SubjectAreaOrg.Role;
import java.sql.SQLException; // Добавлено
import java.util.ArrayList;

public interface ISQLEmployees {
    ArrayList<Employee> get();
    Role insertEmployee(Employee obj);
    Employee getEmployeeById(int employeeId) throws SQLException; // NEW METHOD
    boolean updateProfile(Employee obj, boolean updatePassword); // NEW METHOD
}