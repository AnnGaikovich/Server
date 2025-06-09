package DB;

import SubjectAreaOrg.Role;
import SubjectAreaOrg.Candidates;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ISQLCandidates {

    ArrayList<Candidates> findCandidate(Candidates obj) throws SQLException;
    Role insert(Candidates obj);
    boolean deleteCand(Candidates obj);
    ArrayList<Candidates> get() throws SQLException;
    Candidates getCandidate(Role r) throws SQLException; // Этот метод остается, но будет перегружен
    Candidates getCandidateById(int candidateId) throws SQLException; // NEW METHOD
    ArrayList<Candidates> getEmail(Role r) throws SQLException;
    Candidates getEmail(Candidates s, Role r);
    void setEmail(Candidates s);
    Candidates getNumber(Role r);
    boolean updateCandidateStatus(int candidateId, String newStatus);
    boolean submitResume(int candidateId, int vacancyId, String resumeText, String coverLetter);
    boolean updateProfile(Candidates obj, boolean updatePassword); // NEW METHOD
}