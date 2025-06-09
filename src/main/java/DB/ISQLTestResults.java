// Example/Server/src/main/java/DB/ISQLTestResults.java
package DB;

import SubjectAreaOrg.TestResult;
import java.util.ArrayList;

public interface ISQLTestResults {
    boolean insertTestResult(TestResult obj);
    ArrayList<TestResult> get(); // Для просмотра HR-менеджером (все результаты)
    ArrayList<TestResult> getByCandidateId(int candidateId); // Для просмотра соискателем своих результатов
    boolean updateTestResultStatusAndComment(int testResultId, String newStatus, String hrComment); // НОВЫЙ МЕТОД
}