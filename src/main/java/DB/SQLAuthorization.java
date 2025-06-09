// Example/Server/src/main/java/DB/SQLAuthorization.java
package DB;

import SubjectAreaOrg.Authorization;
import SubjectAreaOrg.Role;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement; // Добавлено для Statement в getAllUsersWithRoles
import java.sql.Types;
import java.util.ArrayList; // Добавлено

public class SQLAuthorization implements ISQLAuthorization {
    private static SQLAuthorization instance;
    private ConnectionDB dbConnection;

    private SQLAuthorization() throws SQLException, ClassNotFoundException {
        dbConnection = ConnectionDB.getInstance();
    }

    public static synchronized SQLAuthorization getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLAuthorization();
        }
        return instance;
    }

    @Override
    public Role getRole(Authorization obj) {
        String proc = "{call find_login(?,?,?,?)}";
        Role r = new Role();
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setString(1, obj.getLogin());
            callableStatement.setString(2, obj.getPassword());
            callableStatement.registerOutParameter(3, Types.INTEGER);
            callableStatement.registerOutParameter(4, Types.VARCHAR);
            callableStatement.execute();

            int id = callableStatement.getInt(3);
            String roleName = callableStatement.getString(4);

            boolean isActive = false;
            if (id != 0) {
                try (PreparedStatement ps = ConnectionDB.dbConnection.prepareStatement("SELECT is_active FROM `keys` WHERE id_keys = ?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            isActive = rs.getBoolean("is_active");
                        }
                    }
                }
            }

            r.setId(id);
            r.setRole(roleName);
            r.setActive(isActive);
            r.setLogin(obj.getLogin());

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при авторизации: " + e.getMessage());
            r.setId(0);
            r.setRole("");
            r.setActive(false);
            r.setLogin("");
        } catch (Exception e) {
            e.printStackTrace();
            r.setId(0);
            r.setRole("");
            r.setActive(false);
            r.setLogin("");
        }
        return r;
    }

    @Override
    public boolean updateUserStatus(int userId, boolean isActive) {
        String proc = "{call update_user_status(?, ?)}";
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, userId);
            callableStatement.setBoolean(2, isActive);
            callableStatement.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении статуса пользователя: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<Role> getAllUsersWithRoles() {
        ArrayList<Role> userRoles = new ArrayList<>();
        String query = "SELECT id_keys, login, role, is_active FROM `keys`";

        try (Statement statement = ConnectionDB.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Role role = new Role();
                role.setId(resultSet.getInt("id_keys"));
                role.setLogin(resultSet.getString("login"));
                role.setRole(resultSet.getString("role"));
                role.setActive(resultSet.getBoolean("is_active"));
                userRoles.add(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error in SQLAuthorization.getAllUsersWithRoles(): " + e.getMessage());
            return null; // Return null to indicate error
        }
        return userRoles;
    }
}