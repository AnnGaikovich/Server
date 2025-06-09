// Example/Server/src/main/java/DB/SQLVacationSchedule.java
package DB;

import SubjectAreaOrg.Vacation;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class SQLVacationSchedule implements ISQLVacationSchedule {

    private static SQLVacationSchedule instance;
    private ConnectionDB dbConnection;

    private SQLVacationSchedule() throws SQLException, ClassNotFoundException {
        dbConnection = ConnectionDB.getInstance();
    }

    public static synchronized SQLVacationSchedule getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLVacationSchedule();
        }
        return instance;
    }

    @Override
    public boolean insertVacation(Vacation obj) {
        String proc = "{call insert_vacation_schedule(?,?,?,?,?)}";
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, obj.getEmployeeId());
            callableStatement.setDate(2, Date.valueOf(obj.getStartDate()));
            callableStatement.setDate(3, Date.valueOf(obj.getEndDate()));
            callableStatement.setInt(4, obj.getYear());
            callableStatement.setString(5, obj.getStatus());
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при вставке графика отпуска: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<Vacation> get() {
        ArrayList<Vacation> vacationList = new ArrayList<>();
        String query = "SELECT vs.id_vacation_schedule, vs.id_employee, e.name, e.surname, vs.start_date, vs.end_date, vs.year, vs.status " +
                "FROM VacationSchedule vs JOIN Employees e ON vs.id_employee = e.id_employee;";
        try (Statement statement = ConnectionDB.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Vacation vacation = new Vacation();
                vacation.setId(resultSet.getInt("id_vacation_schedule"));
                vacation.setEmployeeId(resultSet.getInt("id_employee"));
                vacation.setEmployeeName(resultSet.getString("name") + " " + resultSet.getString("surname"));
                vacation.setStartDate(resultSet.getDate("start_date").toLocalDate());
                vacation.setEndDate(resultSet.getDate("end_date").toLocalDate());
                vacation.setYear(resultSet.getInt("year"));
                vacation.setStatus(resultSet.getString("status"));
                vacationList.add(vacation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vacationList;
    }

    @Override
    public ArrayList<Vacation> getByEmployeeId(int employeeId) { // НОВЫЙ МЕТОД
        ArrayList<Vacation> vacationList = new ArrayList<>();
        String query = "SELECT vs.id_vacation_schedule, vs.id_employee, e.name, e.surname, vs.start_date, vs.end_date, vs.year, vs.status " +
                "FROM VacationSchedule vs JOIN Employees e ON vs.id_employee = e.id_employee " +
                "WHERE vs.id_employee = " + employeeId + ";";
        try (Statement statement = ConnectionDB.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Vacation vacation = new Vacation();
                vacation.setId(resultSet.getInt("id_vacation_schedule"));
                vacation.setEmployeeId(resultSet.getInt("id_employee"));
                vacation.setEmployeeName(resultSet.getString("name") + " " + resultSet.getString("surname"));
                vacation.setStartDate(resultSet.getDate("start_date").toLocalDate());
                vacation.setEndDate(resultSet.getDate("end_date").toLocalDate());
                vacation.setYear(resultSet.getInt("year"));
                vacation.setStatus(resultSet.getString("status"));
                vacationList.add(vacation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error in SQLVacationSchedule.getByEmployeeId(): " + e.getMessage());
        }
        return vacationList;
    }
}