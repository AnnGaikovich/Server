package DB;

import SubjectAreaOrg.Marks;
import java.util.ArrayList;

public interface ISQLMarks {
    boolean insertMark(Marks obj); //Выполняется добавление общей оценки после собеседования(оценка+комментарий)... добавление общей оценки в БД.
    // Функция в worker addMarks

    ArrayList<Marks> get(); //Запрос к БД на отображение всех оценок по каждому пользователю(кандидату).
    // Функция в worker showMarks

}