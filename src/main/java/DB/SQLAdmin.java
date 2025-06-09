package DB;

import SubjectAreaOrg.Admin; //HRmanager - админ
import SubjectAreaOrg.HRmanager;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLAdmin implements ISQLAdmin {
    private static SQLAdmin instance;
    private ConnectionDB dbConnection;

    private SQLAdmin() throws SQLException, ClassNotFoundException {
        dbConnection = ConnectionDB.getInstance();
    }

    public static synchronized SQLAdmin getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLAdmin();
        }
        return instance;
    }

    public ArrayList<Admin> get() throws SQLException {
        String str = "select `keys`.login, `keys`.`password` from admins" +
                " join `keys` on `keys`.id_keys = admins.id_keys;";
        ArrayList<String[]> result = dbConnection.getArrayResult(str);
        ArrayList<Admin> infList = new ArrayList<>();
        for (String[] items : result) {
            Admin admin = new Admin();
            admin.setLogin(items[0]);
            admin.setPassword(items[1]);
            infList.add(admin);
        }
        return infList;
    }
}
