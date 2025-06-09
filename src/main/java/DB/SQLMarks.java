package DB;

import SubjectAreaOrg.Marks;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.ResultSet; // Import added
import java.sql.Statement; // Import added
import java.util.ArrayList;

public class SQLMarks implements ISQLMarks {

    private static SQLMarks instance;
    private ConnectionDB dbConnection;

    private SQLMarks() throws SQLException, ClassNotFoundException {
        dbConnection = ConnectionDB.getInstance();
    }

    public static synchronized SQLMarks getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLMarks();
        }
        return instance;
    }

    @Override
    public boolean insertMark(Marks obj) {
        // Процедура insert_mark теперь ожидает 3 параметра: p_id_interview, p_mark, p_comment
        String proc = "{call insert_mark(?,?,?)}";
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, obj.getInterviewId()); // Передаем ID интервью
            callableStatement.setInt(2, obj.getMark());        // Значение оценки
            callableStatement.setString(3, obj.getComment());  // Комментарий к оценке
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при вставке оценки: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<Marks> get() {
        // Запрос должен получать id_mark, interviewId (как id_interview), mark, comment.
        // Добавляем JOINы, чтобы получить информацию о кандидате, проходящем это интервью.
        String query = "SELECT m.id_mark, m.mark, m.comment, i.id_interview, c.name AS candidate_name, c.surname AS candidate_surname " +
                "FROM Marks m " +
                "JOIN Interviews i ON i.id_interview = m.id_interview " +
                "JOIN Candidates c ON c.id_candidate = i.id_candidate;";

        ArrayList<Marks> marksList = new ArrayList<>();
        try (
                Statement statement = ConnectionDB.dbConnection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Marks mark = new Marks();
                mark.setId(resultSet.getInt("id_mark"));       // id_mark
                mark.setMark(resultSet.getInt("mark"));     // mark
                mark.setComment(resultSet.getString("comment"));                   // comment
                mark.setInterviewId(resultSet.getInt("id_interview")); // id_interview
                // Можно добавить поля для имени/фамилии кандидата в Marks.java если нужно их отображать
                // Например: mark.setCandidateName(resultSet.getString("candidate_name") + " " + resultSet.getString("candidate_surname"));
                marksList.add(mark);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marksList;
    }
}