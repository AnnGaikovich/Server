package DB;

import SubjectAreaOrg.Recommendations;

import java.sql.CallableStatement;
import java.sql.ResultSet; // Добавлено
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;

public class SQLRecommendations implements ISQLRecommendations {

    private static SQLRecommendations instance;
    private ConnectionDB dbConnection;

    private SQLRecommendations() throws SQLException, ClassNotFoundException {
        dbConnection = ConnectionDB.getInstance();
    }

    public static synchronized SQLRecommendations getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLRecommendations();
        }
        return instance;
    }

    @Override
    public boolean insertRecommendation(Recommendations obj) {
        String proc = "{call insert_recommendation(?,?,?)}"; // Теперь ожидаем 3 параметра
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, obj.getCandidateId());     // ID кандидата
            callableStatement.setInt(2, obj.getHrId());            // ID HR-менеджера
            callableStatement.setString(3, obj.getRecommendations()); // Текст рекомендации
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при вставке рекомендации: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<Recommendations> get() {
        String query = "SELECT r.id_recommendation, r.recommendation_text, c.name, c.surname, h.name, h.surname " +
                "FROM Recommendations r " +
                "JOIN Candidates c ON c.id_candidate = r.id_candidate " +
                "LEFT JOIN HR_Managers h ON h.id_HR = r.id_HR;";

        ArrayList<Recommendations> recommendationsList = new ArrayList<>();
        try (
                Statement statement = ConnectionDB.dbConnection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Recommendations recommendation = new Recommendations();
                recommendation.setId(resultSet.getInt("id_recommendation"));
                recommendation.setRecommendations(resultSet.getString("recommendation_text"));
                // Здесь можно добавить поля для имени кандидата и HR-менеджера, если они нужны в SubjectAreaOrg.Recommendations
                // Пока что мы их не добавляли в объект, но можно сделать так:
                // recommendation.setCandidateName(resultSet.getString("c.name") + " " + resultSet.getString("c.surname"));
                // recommendation.setHrManagerName(resultSet.getString("h.name") + " " + resultSet.getString("h.surname"));
                recommendationsList.add(recommendation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recommendationsList;
    }
}