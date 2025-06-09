package DB;

import SubjectAreaOrg.JobPosition;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SQLJobPosition implements ISQLJobPosition {

    private static SQLJobPosition instance;
    private ConnectionDB dbConnection;

    private SQLJobPosition() throws SQLException, ClassNotFoundException {
        dbConnection = ConnectionDB.getInstance();
    }

    public static synchronized SQLJobPosition getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLJobPosition();
        }
        return instance;
    }

    @Override
    public ArrayList<JobPosition> get() {
        ArrayList<JobPosition> positionList = new ArrayList<>();
        String query = "SELECT id_position, name, description FROM JobPositions;";
        try (Statement statement = ConnectionDB.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                JobPosition position = new JobPosition();
                position.setId(resultSet.getInt("id_position"));
                position.setName(resultSet.getString("name"));
                position.setDescription(resultSet.getString("description"));
                positionList.add(position);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return positionList;
    }
}