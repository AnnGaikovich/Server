// Example/Server/src/main/java/DB/SQLHRmanager.java
package DB;

import SubjectAreaOrg.HRmanager;
import SubjectAreaOrg.Role;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

public class SQLHRmanager implements ISQLHRmanager {

    private static SQLHRmanager instance;
    private ConnectionDB dbConnection;

    private SQLHRmanager() throws SQLException, ClassNotFoundException {
        dbConnection = ConnectionDB.getInstance();
    }

    public static synchronized SQLHRmanager getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLHRmanager();
        }
        return instance;
    }

    @Override
    public boolean insert(HRmanager obj) {
        String proc = "{call insert_hrmanager(?,?,?,?,?,?)}";
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setString(1, obj.getFirstname());
            callableStatement.setString(2, obj.getLastname());
            callableStatement.setString(3, obj.getMobile());
            callableStatement.setString(4, obj.getEmail());
            callableStatement.setString(5, obj.getLogin());
            callableStatement.setString(6, obj.getPassword());
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean changeHRmanager(HRmanager obj) {
        String proc = "{call change_hrmanager(?,?,?,?,?,?)}";
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setString(1, obj.getLastlogin());
            callableStatement.setString(2, obj.getFirstname());
            callableStatement.setString(3, obj.getLastname());
            callableStatement.setString(4, obj.getMobile());
            callableStatement.setString(5, obj.getLogin());
            callableStatement.setString(6, obj.getPassword());
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<HRmanager> getHRmanager(Role r) {
        String str = "SELECT `keys`.login, h.name, h.surname, h.mobile, h.email " +
                "FROM HR_Managers h " +
                "JOIN `keys` k ON k.id_keys = h.id_HR " +
                "WHERE k.id_keys = " + r.getId() + ";";

        ArrayList<HRmanager> hrList = new ArrayList<>();
        try { // Добавлена обработка SQLException
            ArrayList<String[]> result = dbConnection.getArrayResult(str);
            for (String[] items : result) {
                HRmanager hrManager = new HRmanager();
                hrManager.setLogin(items[0]);
                hrManager.setFirstname(items[1]);
                hrManager.setLastname(items[2]);
                hrManager.setMobile(items[3]);
                hrManager.setEmail(items[4]);
                hrList.add(hrManager);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error in SQLHRmanager.getHRmanager(): " + e.getMessage());
            return null; // Возвращаем null при ошибке
        }
        return hrList;
    }

    @Override
    public Role getIdByHRmanager(Role obj) {
        String proc = "{call get_idhrmanager_bykeys(?,?)}";
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, obj.getId());
            callableStatement.registerOutParameter(2, Types.INTEGER);

            callableStatement.execute();
            obj.setId(callableStatement.getInt(2));

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

    @Override
    public ArrayList<HRmanager> getAll() {
        // Убрал 'lastlogin' из запроса, так как его нет в БД, согласно ошибке
        String query = "SELECT h.id_HR, h.name, h.surname, h.mobile, h.email, k.login " +
                "FROM HR_Managers h JOIN `keys` k ON h.id_HR = k.id_keys;";
        ArrayList<HRmanager> hrList = new ArrayList<>();
        try (Statement statement = ConnectionDB.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                HRmanager hrManager = new HRmanager();
                hrManager.setId(resultSet.getInt("id_HR"));
                hrManager.setFirstname(resultSet.getString("name")); // <--- Здесь проблема
                hrManager.setLastname(resultSet.getString("surname")); // <--- Здесь проблема
                hrManager.setMobile(resultSet.getString("mobile"));
                hrManager.setEmail(resultSet.getString("email"));
                hrManager.setLogin(resultSet.getString("login"));
                hrList.add(hrManager);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error in SQLHRmanager.getAll(): " + e.getMessage());
            return null;
        }
        return hrList;
    }
}