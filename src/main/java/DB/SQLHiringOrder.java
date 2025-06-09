// Example/Server/src/main/java/DB/SQLHiringOrder.java
package DB;

import SubjectAreaOrg.HiringOrder;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;

public class SQLHiringOrder implements ISQLHiringOrder {

    private static SQLHiringOrder instance;
    private ConnectionDB dbConnection;

    private SQLHiringOrder() throws SQLException, ClassNotFoundException {
        dbConnection = ConnectionDB.getInstance();
    }

    public static synchronized SQLHiringOrder getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLHiringOrder();
        }
        return instance;
    }

    @Override
    public boolean insert(HiringOrder obj, int hrManagerId) {
        String proc = "{call insert_hiring_order(?,?,?,?,?,?)}";
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, obj.getEmployeeId());
            callableStatement.setDate(2, Date.valueOf(obj.getOrderDate()));
            callableStatement.setString(3, obj.getPosition());
            callableStatement.setDouble(4, obj.getSalary());
            callableStatement.setInt(5, obj.getProbationPeriodMonths());
            callableStatement.setInt(6, hrManagerId);
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при вставке приказа о найме: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in SQLHiringOrder.insert(): " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<HiringOrder> get() {
        String query = "SELECT ho.id_order, ho.id_employee, e.name AS employee_name, e.surname AS employee_surname, " +
                "ho.order_date, ho.position, ho.salary, ho.probation_period_months, " +
                "h.name AS hr_name, h.surname AS hr_surname " +
                "FROM HiringOrders ho " +
                "JOIN Employees e ON ho.id_employee = e.id_employee " +
                "LEFT JOIN HR_Managers h ON ho.hr_manager_id = h.id_HR;";

        ArrayList<HiringOrder> orderList = new ArrayList<>();
        try (Statement statement = ConnectionDB.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                HiringOrder order = new HiringOrder();
                order.setId(resultSet.getInt("id_order"));
                order.setEmployeeId(resultSet.getInt("id_employee"));
                order.setEmployeeName(resultSet.getString("employee_name") + " " + resultSet.getString("employee_surname"));
                order.setOrderDate(resultSet.getDate("order_date").toLocalDate());
                order.setPosition(resultSet.getString("position"));
                order.setSalary(resultSet.getDouble("salary"));
                order.setProbationPeriodMonths(resultSet.getInt("probation_period_months"));
                String hrName = resultSet.getString("hr_name");
                String hrSurname = resultSet.getString("hr_surname");
                order.setHrManagerName((hrName != null && hrSurname != null) ? (hrName + " " + hrSurname) : "Неизвестно");
                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error in SQLHiringOrder.get(): " + e.getMessage());
            return null;
        }
        return orderList;
    }

    @Override
    public ArrayList<HiringOrder> getByEmployeeId(int employeeId) { // НОВЫЙ МЕТОД
        String query = "SELECT ho.id_order, ho.id_employee, e.name AS employee_name, e.surname AS employee_surname, " +
                "ho.order_date, ho.position, ho.salary, ho.probation_period_months, " +
                "h.name AS hr_name, h.surname AS hr_surname " +
                "FROM HiringOrders ho " +
                "JOIN Employees e ON ho.id_employee = e.id_employee " +
                "LEFT JOIN HR_Managers h ON ho.hr_manager_id = h.id_HR " +
                "WHERE ho.id_employee = " + employeeId + ";";

        ArrayList<HiringOrder> orderList = new ArrayList<>();
        try (Statement statement = ConnectionDB.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                HiringOrder order = new HiringOrder();
                order.setId(resultSet.getInt("id_order"));
                order.setEmployeeId(resultSet.getInt("id_employee"));
                order.setEmployeeName(resultSet.getString("employee_name") + " " + resultSet.getString("employee_surname"));
                order.setOrderDate(resultSet.getDate("order_date").toLocalDate());
                order.setPosition(resultSet.getString("position"));
                order.setSalary(resultSet.getDouble("salary"));
                order.setProbationPeriodMonths(resultSet.getInt("probation_period_months"));
                String hrName = resultSet.getString("hr_name");
                String hrSurname = resultSet.getString("hr_surname");
                order.setHrManagerName((hrName != null && hrSurname != null) ? (hrName + " " + hrSurname) : "Неизвестно");
                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error in SQLHiringOrder.getByEmployeeId(): " + e.getMessage());
            return null;
        }
        return orderList;
    }
}