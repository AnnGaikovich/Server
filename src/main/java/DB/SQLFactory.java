package DB;

import java.sql.SQLException;

public class SQLFactory extends AbstractFactory {

    public SQLAdmin getAdmin() throws SQLException, ClassNotFoundException {
        return SQLAdmin.getInstance();
    }

    public SQLAuthorization getRole() throws SQLException, ClassNotFoundException {
        return SQLAuthorization.getInstance();
    }

    public SQLRecommendations getRecommendations() throws SQLException, ClassNotFoundException {
        return SQLRecommendations.getInstance();
    }

    public SQLHRmanager getHRManager() throws SQLException, ClassNotFoundException {
        return SQLHRmanager.getInstance();
    }

    public SQLInterviews getInterviews() throws SQLException, ClassNotFoundException {
        return SQLInterviews.getInstance();
    }

    public SQLMarks getMarks() throws SQLException, ClassNotFoundException {
        return SQLMarks.getInstance();
    }

    public SQLVacancies getVacancies() throws SQLException, ClassNotFoundException {
        return SQLVacancies.getInstance();
    }

    public SQLCandidates getCandidates() throws SQLException, ClassNotFoundException {
        return SQLCandidates.getInstance();
    }

    public SQLEmployees getEmployees() throws SQLException, ClassNotFoundException { // New method
        return SQLEmployees.getInstance();
    }

    public SQLVacationSchedule getVacationSchedule() throws SQLException, ClassNotFoundException {
        return SQLVacationSchedule.getInstance();
    }

    public SQLHiringOrder getHiringOrders() throws SQLException, ClassNotFoundException {
        return SQLHiringOrder.getInstance();
    }

    public SQLJobPosition getJobPositions() throws SQLException,ClassNotFoundException {
        return SQLJobPosition.getInstance();
    }

    public SQLTestResults getTestResults() throws SQLException, ClassNotFoundException { // NEW METHOD
        return SQLTestResults.getInstance();
    }

    public SQLSubmittedResumes getSubmittedResumes() throws SQLException, ClassNotFoundException { // NEW METHOD
        return SQLSubmittedResumes.getInstance();
    }
}