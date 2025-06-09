package DB;

import SubjectAreaOrg.HRmanager;
import SubjectAreaOrg.Role;
import java.util.ArrayList;

public interface ISQLHRmanager {

    boolean insert(HRmanager obj);

    boolean changeHRmanager(HRmanager obj);

    ArrayList<HRmanager> getHRmanager(Role r);

    Role getIdByHRmanager(Role obj);

    ArrayList<HRmanager> getAll(); // NEW METHOD
}