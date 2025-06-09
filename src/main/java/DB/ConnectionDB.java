package DB;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class ConnectionDB {
    private static ConnectionDB instance;

    private String dbHost;
    private String dbPort;
    private String dbUser;
    private String dbPass;
    private String dbName;

    ArrayList<String[]> masResult;

    public static Connection dbConnection; // Статическое поле, которое мы будем мокировать
    private Statement statement;
    // private ResultSet resultSet; // Это поле не нужно здесь, так как ResultSet создается внутри getArrayResult

    // Приватный конструктор для Singleton (для продакшн-кода)
    private ConnectionDB() throws ClassNotFoundException, SQLException {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties in server resources.");
                throw new RuntimeException("config.properties not found for server database connection.");
            }
            prop.load(input);

            dbHost = prop.getProperty("db.host");
            dbPort = prop.getProperty("db.port");
            dbUser = prop.getProperty("db.user");
            dbPass = prop.getProperty("db.pass");
            dbName = prop.getProperty("db.name");
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Failed to load database configuration from config.properties", e);
        }

        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?verifyServerCertificate=false" +
                "&useSSL=false" +
                "&requireSSL=false" +
                "&useLegacyDatetimeCode=false" +
                "&amp" +
                "&serverTimezone=UTC" +
                "&allowPublicKeyRetrieval=true";

        Class.forName("com.mysql.cj.jdbc.Driver");

        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);
        statement = dbConnection.createStatement();
    }

    // Публичный конструктор для тестов (позволяет инжектировать mock Connection)
    public ConnectionDB(Connection connection) throws SQLException {
        ConnectionDB.dbConnection = connection; // Устанавливаем статическое поле
        this.statement = connection.createStatement();
    }

    // Сеттер для установки mock-соединения в тестах
    public static void setTestConnection(Connection testConnection) throws SQLException {
        dbConnection = testConnection;
        // Если 'instance' уже создан, обновим его statement.
        // Это важно, так как getInstance() возвращает один и тот же 'instance'.
        if (instance != null) {
            instance.statement = testConnection.createStatement();
        }
    }


    public void execute(String query) {
        try {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized ConnectionDB getInstance() throws SQLException, ClassNotFoundException {
        // В тестах мы будем мокировать этот метод, чтобы он возвращал наш тестовый экземпляр
        // В продакшн-коде, он будет использовать приватный конструктор
        if (instance == null) {
            instance = new ConnectionDB();
        }
        return instance;
    }

    public ArrayList<String[]> getArrayResult(String str) throws SQLException {
        masResult = new ArrayList<>();
        // Здесь используется statement, который должен быть установлен через ConnectionDB(Connection) или setTestConnection
        try (ResultSet localResultSet = statement.executeQuery(str)) { // Используем локальную переменную для ResultSet
            ResultSetMetaData metaData = localResultSet.getMetaData(); // Получаем метаданные из локального ResultSet
            int count = metaData.getColumnCount();

            while (localResultSet.next()) {
                String[] arrayString = new String[count];
                for (int i = 1; i <= count; i++)
                    arrayString[i - 1] = localResultSet.getString(i);

                masResult.add(arrayString);
            }
        }
        return masResult;
    }
}