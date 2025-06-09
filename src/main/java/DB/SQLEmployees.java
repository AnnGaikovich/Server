package DB;

import SubjectAreaOrg.Employee;
import SubjectAreaOrg.Role;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Types;
import java.util.ArrayList;

public class SQLEmployees implements ISQLEmployees {

    private static SQLEmployees instance;
    private ConnectionDB dbConnection;

    private SQLEmployees() throws SQLException, ClassNotFoundException {
        dbConnection = ConnectionDB.getInstance();
    }

    public static synchronized SQLEmployees getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLEmployees();
        }
        return instance;
    }

    @Override
    public ArrayList<Employee> get() {
        String str = "SELECT k.login, e.id_employee, e.name, e.surname, e.mobile, e.email " +
                "FROM Employees e " +
                "JOIN `keys` k ON k.id_keys = e.id_employee;";

        ArrayList<Employee> employeeList = new ArrayList<>();
        try { // Добавлена обработка SQLException
            ArrayList<String[]> result = dbConnection.getArrayResult(str);
            for (String[] items : result) {
                Employee employee = new Employee();
                employee.setLogin(items[0]);
                employee.setId(Integer.parseInt(items[1]));
                employee.setName(items[2]);
                employee.setSurname(items[3]);
                employee.setMobile(items[4]);
                employee.setEmail(items[5]);
                employeeList.add(employee);
            }
        } catch (SQLException | NumberFormatException e) { // Ловим SQL и NumberFormatException
            e.printStackTrace();
            System.err.println("Error in SQLEmployees.get(): " + e.getMessage());
            return null; // Возвращаем null при ошибке
        }
        return employeeList;
    }

    // NEW METHOD: getEmployeeById
    @Override
    public Employee getEmployeeById(int employeeId) throws SQLException {
        String query = "SELECT k.login, e.id_employee, e.name, e.surname, e.mobile, e.email " +
                "FROM Employees e " +
                "JOIN `keys` k ON k.id_keys = e.id_employee " +
                "WHERE e.id_employee = " + employeeId + ";";

        ArrayList<String[]> result = dbConnection.getArrayResult(query);
        Employee employee = null;

        if (!result.isEmpty()) {
            String[] items = result.get(0);
            employee = new Employee();
            employee.setLogin(items[0]);
            employee.setId(Integer.parseInt(items[1]));
            employee.setName(items[2]);
            employee.setSurname(items[3]);
            employee.setMobile(items[4]);
            employee.setEmail(items[5]);
        }
        return employee;
    }


    @Override
    public Role insertEmployee(Employee obj) {
        String proc = "{call insert_employee(?,?,?,?,?,?,?, ?,?)}"; // Процедура должна быть такой же, как insert_candidate, но для Employees
        Role r = new Role();

        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setString(1, obj.getName());
            callableStatement.setString(2, obj.getSurname());
            callableStatement.setString(3, obj.getMobile());
            callableStatement.setString(4, obj.getEmail());
            callableStatement.setString(5, obj.getLogin());
            callableStatement.setString(6, obj.getPassword());
            callableStatement.registerOutParameter(7, Types.INTEGER); // OUT: ID роли
            callableStatement.registerOutParameter(8, Types.VARCHAR); // OUT: Role name
            callableStatement.registerOutParameter(9, Types.BOOLEAN); // OUT: is_active (new parameter, default true)

            callableStatement.execute();
            r.setId(callableStatement.getInt(7));
            r.setRole(callableStatement.getString(8));
            r.setActive(callableStatement.getBoolean(9)); // Получаем статус активности
            r.setLogin(obj.getLogin()); // Устанавливаем логин

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных: Пользователь с таким логином уже существует.");
            r.setId(0); // Сбросить ID, чтобы показать ошибку
            r.setRole(""); // Сбросить роль
            r.setActive(false);
            r.setLogin("");
        } catch (Exception e) {
            e.printStackTrace();
            r.setId(0); // Сбросить ID, чтобы показать ошибку
            r.setRole(""); // Сбросить роль
            r.setActive(false);
            r.setLogin("");
        }

        return r;
    }

    // NEW METHOD: updateProfile for Employee
    @Override
    public boolean updateProfile(Employee obj, boolean updatePassword) {
        String proc = "{call update_employee_profile(?,?,?,?,?,?,?)}"; // New stored procedure for update
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, obj.getId());
            callableStatement.setString(2, obj.getName());
            callableStatement.setString(3, obj.getSurname());
            callableStatement.setString(4, obj.getMobile());
            callableStatement.setString(5, obj.getEmail());
            // Пароль обновляется только если updatePassword = true
            callableStatement.setString(6, updatePassword ? obj.getPassword() : null);
            callableStatement.setBoolean(7, updatePassword); // Флаг для процедуры

            callableStatement.execute();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при обновлении профиля сотрудника: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}