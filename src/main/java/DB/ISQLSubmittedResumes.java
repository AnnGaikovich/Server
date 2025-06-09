// Example/Server/src/main/java/DB/ISQLSubmittedResumes.java
package DB;

import SubjectAreaOrg.SubmittedResume;
import java.util.ArrayList;

public interface ISQLSubmittedResumes {
    boolean insert(SubmittedResume obj);
    ArrayList<SubmittedResume> getByCandidateId(int candidateId);
    ArrayList<SubmittedResume> getByEmployeeId(int employeeId);
    ArrayList<SubmittedResume> get(); // Для HR-менеджера, если понадобится просматривать все резюме
    boolean updateStatusAndFeedback(int resumeId, String newStatus, String hrFeedback); // Для HR-менеджера, чтобы обновлять статус и давать обратную связь
}