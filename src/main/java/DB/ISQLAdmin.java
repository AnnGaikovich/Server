package DB;
import SubjectAreaOrg.Admin;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ISQLAdmin {
    ArrayList<Admin> get() throws SQLException;
}
