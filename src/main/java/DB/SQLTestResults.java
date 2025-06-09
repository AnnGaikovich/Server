package DB;

import SubjectAreaOrg.TestResult;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SQLTestResults implements ISQLTestResults {

    private static SQLTestResults instance;
    private ConnectionDB dbConnection;

    private SQLTestResults() throws SQLException, ClassNotFoundException {
        dbConnection = ConnectionDB.getInstance();
    }

    public static synchronized SQLTestResults getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLTestResults();
        }
        return instance;
    }

    @Override
    public boolean insertTestResult(TestResult obj) {
        String proc = "{call insert_test_result(?,?,?,?,?)}"; // id_candidate, id_vacancy, question, answer, status
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, obj.getCandidateId());
            callableStatement.setInt(2, obj.getVacancyId());
            callableStatement.setString(3, obj.getTestQuestion());
            callableStatement.setString(4, obj.getCandidateAnswer());
            callableStatement.setString(5, obj.getStatus());
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при вставке результата теста: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<TestResult> get() { // Для HR-менеджера
        ArrayList<TestResult> testResultsList = new ArrayList<>();
        String query = "SELECT tr.id_test_result, tr.id_candidate, c.name AS candidate_name, c.surname AS candidate_surname, " +
                "tr.id_job, v.name AS vacancy_name, tr.test_question, tr.candidate_answer, " +
                "tr.submission_date, tr.status, tr.hr_comment " +
                "FROM TestResults tr " +
                "JOIN Candidates c ON tr.id_candidate = c.id_candidate " +
                "JOIN Vacancies v ON tr.id_job = v.id_job;";
        try (Statement statement = ConnectionDB.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                TestResult testResult = new TestResult();
                testResult.setId(resultSet.getInt("id_test_result"));
                testResult.setCandidateId(resultSet.getInt("id_candidate"));
                // Здесь можно добавить поля для имени кандидата и вакансии в TestResult.java, если нужны
                testResult.setVacancyId(resultSet.getInt("id_job"));
                testResult.setTestQuestion(resultSet.getString("test_question"));
                testResult.setCandidateAnswer(resultSet.getString("candidate_answer"));
                testResult.setSubmissionDate(resultSet.getTimestamp("submission_date").toLocalDateTime());
                testResult.setStatus(resultSet.getString("status"));
                testResult.setHrComment(resultSet.getString("hr_comment"));
                testResultsList.add(testResult);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return testResultsList;
    }

    @Override
    public boolean updateTestResultStatusAndComment(int testResultId, String newStatus, String hrComment) { // НОВЫЙ МЕТОД
        // Процедура для обновления статуса и комментария HR в таблице TestResults
        String proc = "{call update_test_result_status_comment(?,?,?)}"; // Нужно создать эту процедуру в БД
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, testResultId);
            callableStatement.setString(2, newStatus);
            callableStatement.setString(3, hrComment);
            callableStatement.execute();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при обновлении результата теста: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<TestResult> getByCandidateId(int candidateId) {
        ArrayList<TestResult> testResultsList = new ArrayList<>();
        String query = "SELECT tr.id_test_result, tr.id_candidate, tr.id_job, v.name AS vacancy_name, " +
                "tr.test_question, tr.candidate_answer, tr.submission_date, tr.status, tr.hr_comment " +
                "FROM TestResults tr " +
                "JOIN Vacancies v ON tr.id_job = v.id_job " +
                "WHERE tr.id_candidate = " + candidateId + ";";
        try (Statement statement = ConnectionDB.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                TestResult testResult = new TestResult();
                testResult.setId(resultSet.getInt("id_test_result"));
                testResult.setCandidateId(resultSet.getInt("id_candidate"));
                testResult.setVacancyId(resultSet.getInt("id_job"));
                // testResult.setVacancyName(resultSet.getString("vacancy_name")); // Если нужно добавить в SubjectAreaOrg.TestResult
                testResult.setTestQuestion(resultSet.getString("test_question"));
                testResult.setCandidateAnswer(resultSet.getString("candidate_answer"));
                testResult.setSubmissionDate(resultSet.getTimestamp("submission_date").toLocalDateTime());
                testResult.setStatus(resultSet.getString("status"));
                testResult.setHrComment(resultSet.getString("hr_comment"));
                testResultsList.add(testResult);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return testResultsList;
    }
}