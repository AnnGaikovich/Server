// Example/Server/src/main/java/DB/SQLSubmittedResumes.java
package DB;

import SubjectAreaOrg.SubmittedResume;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SQLSubmittedResumes implements ISQLSubmittedResumes {

    private static SQLSubmittedResumes instance;
    private ConnectionDB dbConnection;

    private SQLSubmittedResumes() throws SQLException, ClassNotFoundException {
        dbConnection = ConnectionDB.getInstance();
    }

    public static synchronized SQLSubmittedResumes getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLSubmittedResumes();
        }
        return instance;
    }

    @Override
    public boolean insert(SubmittedResume obj) {
        String proc = "{call insert_submitted_resume(?,?,?,?,?)}"; // Добавили p_id_employee
        System.out.println("SQLSubmittedResumes: Попытка вставки резюме для кандидата ID: " + obj.getCandidateId() + ", сотрудника ID: " + obj.getEmployeeId() + ", вакансия ID: " + obj.getVacancyId());
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            // Если это заявка от кандидата, employeeId будет 0. Если от сотрудника, candidateId будет 0.
            callableStatement.setInt(1, obj.getCandidateId());
            callableStatement.setInt(2, obj.getEmployeeId()); // НОВЫЙ ПАРАМЕТР
            callableStatement.setInt(3, obj.getVacancyId());
            callableStatement.setString(4, obj.getResumeText());
            callableStatement.setString(5, obj.getCoverLetter());
            callableStatement.execute();
            System.out.println("SQLSubmittedResumes: Резюме успешно вставлено.");
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("SQLSubmittedResumes: Ошибка целостности данных при вставке отправленного резюме: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("SQLSubmittedResumes: Неизвестная ошибка при вставке резюме: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<SubmittedResume> getByCandidateId(int candidateId) {
        ArrayList<SubmittedResume> resumeList = new ArrayList<>();
        String query = "SELECT sr.id_submitted_resume, sr.id_candidate, c.name AS candidate_name, c.surname AS candidate_surname, " +
                "sr.id_employee, e.name AS employee_name, e.surname AS employee_surname, " + // Добавлено
                "sr.id_job, v.name AS vacancy_name, sr.resume_text, sr.cover_letter, sr.submission_date, sr.status, sr.hr_feedback " +
                "FROM SubmittedResumes sr " +
                "LEFT JOIN Candidates c ON sr.id_candidate = c.id_candidate " + // LEFT JOIN, так как id_candidate может быть NULL
                "LEFT JOIN Employees e ON sr.id_employee = e.id_employee " +     // НОВЫЙ LEFT JOIN
                "JOIN Vacancies v ON sr.id_job = v.id_job " +
                "WHERE sr.id_candidate = " + candidateId + ";"; // Запрос только для кандидата

        try (Statement statement = ConnectionDB.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                SubmittedResume resume = new SubmittedResume();
                resume.setId(resultSet.getInt("id_submitted_resume"));

                int candId = resultSet.getInt("id_candidate");
                if (!resultSet.wasNull()) {
                    resume.setCandidateId(candId);
                    resume.setCandidateName(resultSet.getString("candidate_name") + " " + resultSet.getString("candidate_surname"));
                } else {
                    resume.setCandidateId(0);
                    resume.setCandidateName("");
                }

                int empId = resultSet.getInt("id_employee"); // Читаем employeeId
                if (!resultSet.wasNull()) {
                    resume.setEmployeeId(empId);
                    resume.setEmployeeName(resultSet.getString("employee_name") + " " + resultSet.getString("employee_surname"));
                } else {
                    resume.setEmployeeId(0);
                    resume.setEmployeeName("");
                }

                resume.setVacancyId(resultSet.getInt("id_job"));
                resume.setVacancyName(resultSet.getString("vacancy_name"));
                resume.setResumeText(resultSet.getString("resume_text"));
                resume.setCoverLetter(resultSet.getString("cover_letter"));
                resume.setSubmissionDate(resultSet.getTimestamp("submission_date").toLocalDateTime());
                resume.setStatus(resultSet.getString("status"));
                resume.setHrFeedback(resultSet.getString("hr_feedback"));
                resumeList.add(resume);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resumeList;
    }

    @Override
    public ArrayList<SubmittedResume> getByEmployeeId(int employeeId) { // НОВЫЙ МЕТОД
        ArrayList<SubmittedResume> resumeList = new ArrayList<>();
        String query = "SELECT sr.id_submitted_resume, sr.id_candidate, c.name AS candidate_name, c.surname AS candidate_surname, " +
                "sr.id_employee, e.name AS employee_name, e.surname AS employee_surname, " +
                "sr.id_job, v.name AS vacancy_name, sr.resume_text, sr.cover_letter, sr.submission_date, sr.status, sr.hr_feedback " +
                "FROM SubmittedResumes sr " +
                "LEFT JOIN Candidates c ON sr.id_candidate = c.id_candidate " +
                "LEFT JOIN Employees e ON sr.id_employee = e.id_employee " +
                "JOIN Vacancies v ON sr.id_job = v.id_job " +
                "WHERE sr.id_employee = " + employeeId + ";"; // Запрос только для сотрудника

        try (Statement statement = ConnectionDB.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                SubmittedResume resume = new SubmittedResume();
                resume.setId(resultSet.getInt("id_submitted_resume"));

                int candId = resultSet.getInt("id_candidate");
                if (!resultSet.wasNull()) {
                    resume.setCandidateId(candId);
                    resume.setCandidateName(resultSet.getString("candidate_name") + " " + resultSet.getString("candidate_surname"));
                } else {
                    resume.setCandidateId(0);
                    resume.setCandidateName("");
                }

                int empId = resultSet.getInt("id_employee");
                if (!resultSet.wasNull()) {
                    resume.setEmployeeId(empId);
                    resume.setEmployeeName(resultSet.getString("employee_name") + " " + resultSet.getString("employee_surname"));
                } else {
                    resume.setEmployeeId(0);
                    resume.setEmployeeName("");
                }

                resume.setVacancyId(resultSet.getInt("id_job"));
                resume.setVacancyName(resultSet.getString("vacancy_name"));
                resume.setResumeText(resultSet.getString("resume_text"));
                resume.setCoverLetter(resultSet.getString("cover_letter"));
                resume.setSubmissionDate(resultSet.getTimestamp("submission_date").toLocalDateTime());
                resume.setStatus(resultSet.getString("status"));
                resume.setHrFeedback(resultSet.getString("hr_feedback"));
                resumeList.add(resume);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error in SQLSubmittedResumes.getByEmployeeId(): " + e.getMessage());
        }
        return resumeList;
    }

    @Override
    public ArrayList<SubmittedResume> get() { // Для HR-менеджера
        ArrayList<SubmittedResume> resumeList = new ArrayList<>();
        String query = "SELECT sr.id_submitted_resume, sr.id_candidate, c.name AS candidate_name, c.surname AS candidate_surname, " +
                "sr.id_employee, e.name AS employee_name, e.surname AS employee_surname, " +
                "sr.id_job, v.name AS vacancy_name, sr.resume_text, sr.cover_letter, sr.submission_date, sr.status, sr.hr_feedback " +
                "FROM SubmittedResumes sr " +
                "LEFT JOIN Candidates c ON sr.id_candidate = c.id_candidate " +
                "LEFT JOIN Employees e ON sr.id_employee = e.id_employee " +
                "JOIN Vacancies v ON sr.id_job = v.id_job;";
        try (Statement statement = ConnectionDB.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                SubmittedResume resume = new SubmittedResume();
                resume.setId(resultSet.getInt("id_submitted_resume"));

                int candId = resultSet.getInt("id_candidate");
                if (!resultSet.wasNull()) {
                    resume.setCandidateId(candId);
                    resume.setCandidateName(resultSet.getString("candidate_name") + " " + resultSet.getString("candidate_surname"));
                } else {
                    resume.setCandidateId(0);
                    resume.setCandidateName("");
                }

                int empId = resultSet.getInt("id_employee");
                if (!resultSet.wasNull()) {
                    resume.setEmployeeId(empId);
                    resume.setEmployeeName(resultSet.getString("employee_name") + " " + resultSet.getString("employee_surname"));
                } else {
                    resume.setEmployeeId(0);
                    resume.setEmployeeName("");
                }

                resume.setVacancyId(resultSet.getInt("id_job"));
                resume.setVacancyName(resultSet.getString("vacancy_name"));
                resume.setResumeText(resultSet.getString("resume_text"));
                resume.setCoverLetter(resultSet.getString("cover_letter"));
                resume.setSubmissionDate(resultSet.getTimestamp("submission_date").toLocalDateTime());
                resume.setStatus(resultSet.getString("status"));
                resume.setHrFeedback(resultSet.getString("hr_feedback"));
                resumeList.add(resume);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resumeList;
    }

    @Override
    public boolean updateStatusAndFeedback(int resumeId, String newStatus, String hrFeedback) {
        String proc = "{call update_submitted_resume_status_feedback(?,?,?)}";
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, resumeId);
            callableStatement.setString(2, newStatus);
            callableStatement.setString(3, hrFeedback);
            callableStatement.execute();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при обновлении статуса резюме: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}