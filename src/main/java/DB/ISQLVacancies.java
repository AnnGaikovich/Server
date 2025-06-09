package DB;

import SubjectAreaOrg.Vacancies;
import java.util.ArrayList;

public interface ISQLVacancies {

    boolean insert(Vacancies obj);

    ArrayList<Vacancies> get(); //Запрос к БД на получение доступных вакансий.
    // Функция в worker showVacancies

    ArrayList<Vacancies> find(Vacancies c); //Запрос к БД на поиск вакансий...
    // Функция в worker findVacancies

    boolean insertVacancy(Vacancies obj); //Выполняется добавление вакансии... добавление вакансии в БД.
    // Функция в worker addVacancies

    boolean registration(Vacancies obj); //Выполняется регистрация вакансии... инициализация вакансии.
    // Функция в worker regVacancies

    boolean change(Vacancies obj); //Выполняется изменение вакансии...
    // Функция в worker changeVacancies

}
