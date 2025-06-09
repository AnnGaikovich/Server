package DB;

import SubjectAreaOrg.Recommendations;

import java.util.ArrayList;

public interface ISQLRecommendations {

    boolean insertRecommendation(Recommendations obj); //Выполняется добавление рекомендаций после собеседования... добавление рекомендаций в БД.
    // Функция в worker addRecommendations

    ArrayList<Recommendations> get(); //Запрос к БД на отображение всех рекомендаций по каждому пользователю(кандидату).
    // Функция в worker showRecommendations
}
