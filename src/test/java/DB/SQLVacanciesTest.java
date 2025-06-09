package DB;

import SubjectAreaOrg.Vacancies;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SQLVacanciesTest {

    private SQLVacancies sqlVacancies;
    private Connection mockConnection;
    private Statement mockStatement;
    private CallableStatement mockCallableStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        mockCallableStatement = mock(CallableStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockConnection.prepareCall(anyString())).thenReturn(mockCallableStatement);

        ConnectionDB.setTestConnection(mockConnection);
        sqlVacancies = SQLVacancies.getInstance();
    }

    @Test
    void testInsertVacancy_success() throws SQLException {
        Vacancies vacancy = new Vacancies();
        vacancy.setName("Java Dev");
        vacancy.setDescription("Backend position");
        vacancy.setDate(LocalDate.now());
        vacancy.setId(1);

        when(mockCallableStatement.execute()).thenReturn(true);
        doNothing().when(mockCallableStatement).setString(anyInt(), anyString());
        doNothing().when(mockCallableStatement).setDate(anyInt(), any(Date.class));
        doNothing().when(mockCallableStatement).setInt(anyInt(), anyInt());

        boolean result = sqlVacancies.insert(vacancy);

        assertTrue(result);
        verify(mockCallableStatement, times(1)).execute();
    }

    @Test
    void testGetVacancies_success() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id_job")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Java Dev");
        when(mockResultSet.getString("description")).thenReturn("Backend job");
        when(mockResultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(mockResultSet.getString("h.name")).thenReturn("Ivan");
        when(mockResultSet.getString("h.surname")).thenReturn("Petrov");

        ArrayList<Vacancies> result = sqlVacancies.get();

        assertEquals(1, result.size());
        assertEquals("Java Dev", result.get(0).getName());
    }

    @Test
    void testFindVacancy_byName() throws SQLException {
        Vacancies search = new Vacancies();
        search.setName("Java");

        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id_job")).thenReturn(2);
        when(mockResultSet.getString("name")).thenReturn("Java Dev");
        when(mockResultSet.getString("description")).thenReturn("Search logic");
        when(mockResultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(mockResultSet.getString("h.name")).thenReturn(null);
        when(mockResultSet.getString("h.surname")).thenReturn(null);

        ArrayList<Vacancies> result = sqlVacancies.find(search);

        assertFalse(result.isEmpty());
        assertEquals("Не назначен", result.get(0).getHrManagerName());
    }

    @Test
    void testChangeVacancy_success() throws SQLException {
        Vacancies vacancy = new Vacancies();
        vacancy.setId(3);
        vacancy.setName("QA");
        vacancy.setDescription("Test role");
        vacancy.setDate(LocalDate.of(2025, 6, 2));

        when(mockCallableStatement.execute()).thenReturn(true);

        boolean result = sqlVacancies.change(vacancy);

        assertTrue(result);
        verify(mockCallableStatement, times(1)).setInt(1, 3);
        verify(mockCallableStatement, times(1)).execute();
    }

    @Test
    void testRegistration_success() throws SQLException {
        Vacancies vacancy = new Vacancies();
        vacancy.setName("RegTest");
        vacancy.setDescription("Registered vacancy");
        vacancy.setDate(LocalDate.now());
        vacancy.setId(5);

        when(mockCallableStatement.execute()).thenReturn(true);

        boolean result = sqlVacancies.registration(vacancy);

        assertTrue(result);
    }
}
