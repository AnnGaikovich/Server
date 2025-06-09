package DB;

import SubjectAreaOrg.Interviews;
import java.util.ArrayList;

public interface ISQLInterviews {

    boolean insert(Interviews obj);
    ArrayList<Interviews> get(); //Запрос к БД на получение доступных собеседований.
    // Функция в worker showInterviews

    ArrayList<Interviews> find(Interviews c); //Запрос к БД на поиск собеседований...
    // Функция в worker findInterviews

    boolean insertInterview(Interviews obj); //Выполняется добавление собеседования... добавление собеседования в БД.
    // Функция в worker addInterviews

    boolean registration(Interviews obj); //Выполняется регистрация пользователя(кандидата) на собеседование... инициализация собеседования.
    // Функция в worker regInterviews

    boolean change(Interviews obj); //Выполняется изменение собеседования...
    // Функция в worker changeInterviews

}

//import SchoolOrg.Courses;

//import java.util.ArrayList;

//public interface ISQLCourses {
//    boolean insert(Courses obj);
//    ArrayList<Courses> get();
//    ArrayList<Courses> find(Courses c);
//    boolean insertCourse(Courses obj);
//    boolean registration(Courses obj);
//}