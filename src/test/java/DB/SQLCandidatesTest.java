package DB;

import SubjectAreaOrg.Candidates;
import SubjectAreaOrg.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SQLCandidatesTest {

    private SQLCandidates sqlCandidates;
    private Connection mockConnection;
    private Statement mockStatement; // ДОБАВЛЕНО: mockStatement как поле класса
    private ResultSet mockResultSet;
    private CallableStatement mockCallableStatement;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        // Создаем mock объекты для JDBC
        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class); // Инициализация mockStatement
        mockResultSet = mock(ResultSet.class);
        mockCallableStatement = mock(CallableStatement.class);

        // Настраиваем поведение mockConnection
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockConnection.prepareCall(anyString())).thenReturn(mockCallableStatement);

        // Устанавливаем mockConnection в статическое поле ConnectionDB.dbConnection
        ConnectionDB.setTestConnection(mockConnection);
        sqlCandidates = SQLCandidates.getInstance(); // Теперь getInstance получит ConnectionDB с mockConnection
    }

    @Test
    void testFindCandidate_found() throws SQLException {
        // Arrange
        Candidates searchObj = new Candidates();
        searchObj.setLogin("testlogin");
        searchObj.setStatus("Активен");

        // ResultSetMetaData нужен для getColumnCount() в ConnectionDB.getArrayResult()
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(8); // Количество колонок в запросе findCandidate

        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet); // Здесь используем mockStatement
        when(mockResultSet.next())
                .thenReturn(true) // Первый вызов next() - есть результат
                .thenReturn(false); // Второй вызов next() - нет больше результатов

        when(mockResultSet.getString(1)).thenReturn("testlogin"); // login
        when(mockResultSet.getString(2)).thenReturn("1"); // id_candidate (как строка для getArrayResult)
        when(mockResultSet.getString(3)).thenReturn("Test"); // name
        when(mockResultSet.getString(4)).thenReturn("User"); // surname
        when(mockResultSet.getString(5)).thenReturn("111222333"); // mobile
        when(mockResultSet.getString(6)).thenReturn("1990-01-01"); // birthday
        when(mockResultSet.getString(7)).thenReturn("test@example.com"); // email
        when(mockResultSet.getString(8)).thenReturn("Активен"); // status

        // Act
        ArrayList<Candidates> result = sqlCandidates.findCandidate(searchObj);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        Candidates foundCandidate = result.get(0);
        assertEquals("testlogin", foundCandidate.getLogin());
        assertEquals("Test", foundCandidate.getName());
        assertEquals("Активен", foundCandidate.getStatus());

        // Проверяем, что запрос был выполнен
        verify(mockStatement, times(1)).executeQuery(anyString());
    }

    @Test
    void testFindCandidate_notFound() throws SQLException {
        // Arrange
        Candidates searchObj = new Candidates();
        searchObj.setLogin("nonexistent");

        // ResultSetMetaData нужен, даже если нет результатов, т.к. getArrayResult его вызывает
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(8);

        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet); // Здесь используем mockStatement
        when(mockResultSet.next()).thenReturn(false); // Нет результатов

        // Act
        ArrayList<Candidates> result = sqlCandidates.findCandidate(searchObj);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty()); // Ожидаем пустой список

        verify(mockStatement, times(1)).executeQuery(anyString());
    }

    @Test
    void testInsertCandidate_success() throws SQLException {
        // Arrange
        Candidates candidate = new Candidates();
        candidate.setName("New");
        candidate.setSurname("Candidate");
        candidate.setMobile("000");
        candidate.setBirthday(LocalDate.of(2000, 1, 1));
        candidate.setEmail("new@example.com");
        candidate.setLogin("newuser");
        candidate.setPassword("hashedpass");

        // Настраиваем CallableStatement для возврата OUT параметров
        when(mockCallableStatement.execute()).thenReturn(true);
        // Используем doNothing() для setInt/setString, т.к. они void методы
        doNothing().when(mockCallableStatement).setString(anyInt(), anyString());
        doNothing().when(mockCallableStatement).setInt(anyInt(), anyInt());
        doNothing().when(mockCallableStatement).setDate(anyInt(), any(Date.class));

        when(mockCallableStatement.getInt(8)).thenReturn(100); // id_keys
        when(mockCallableStatement.getString(9)).thenReturn("Candidate"); // role

        // Act
        Role resultRole = sqlCandidates.insert(candidate);

        // Assert
        assertNotNull(resultRole);
        assertEquals(100, resultRole.getId());
        assertEquals("Candidate", resultRole.getRole());
        assertEquals("newuser", resultRole.getLogin());

        // Проверяем, что CallableStatement был вызван с нужными параметрами
        verify(mockCallableStatement, times(1)).setString(1, "New");
        verify(mockCallableStatement, times(1)).setString(6, "newuser");
        verify(mockCallableStatement, times(1)).setString(7, "hashedpass");
        verify(mockCallableStatement, times(1)).execute();
    }

    @Test
    void testInsertCandidate_duplicateLogin() throws SQLException {
        // Arrange
        Candidates candidate = new Candidates();
        candidate.setLogin("existinguser");
        // Имитируем ошибку целостности данных (например, дубликат логина)
        when(mockCallableStatement.execute()).thenThrow(new SQLIntegrityConstraintViolationException("Duplicate entry"));

        // Act
        Role resultRole = sqlCandidates.insert(candidate);

        // Assert
        assertNotNull(resultRole);
        assertEquals(0, resultRole.getId()); // ID должен быть 0 при ошибке
        assertTrue(resultRole.getRole().isEmpty()); // Роль должна быть пустой
    }

    @Test
    void testUpdateCandidateStatus_success() throws SQLException {
        // Arrange
        int candidateId = 1;
        String newStatus = "Принят";

        // Настраиваем CallableStatement
        when(mockCallableStatement.execute()).thenReturn(true);
        doNothing().when(mockCallableStatement).setInt(anyInt(), anyInt());
        doNothing().when(mockCallableStatement).setString(anyInt(), anyString());

        // Act
        boolean result = sqlCandidates.updateCandidateStatus(candidateId, newStatus);

        // Assert
        assertTrue(result);

        // Проверяем, что CallableStatement был вызван с нужными параметрами
        verify(mockCallableStatement, times(1)).setInt(1, candidateId);
        verify(mockCallableStatement, times(1)).setString(2, newStatus);
        verify(mockCallableStatement, times(1)).execute();
    }

    @Test
    void testUpdateCandidateStatus_failure() throws SQLException {
        // Arrange
        int candidateId = 1;
        String newStatus = "Принят";

        // Имитируем ошибку при выполнении CallableStatement
        when(mockCallableStatement.execute()).thenThrow(new SQLException("DB error"));
        doNothing().when(mockCallableStatement).setInt(anyInt(), anyInt());
        doNothing().when(mockCallableStatement).setString(anyInt(), anyString());

        // Act
        boolean result = sqlCandidates.updateCandidateStatus(candidateId, newStatus);

        // Assert
        assertFalse(result); // Ожидаем false при ошибке
    }
}