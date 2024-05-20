package ru.spbstu.dlstats.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.spbstu.dlstats.dto.StudentDTO;
import ru.spbstu.dlstats.repositorys.ExelDataRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ExelDataService {
    private final ExelDataRepository exelDataRepository;

    /**
     * Конструктор класса ObsDataService.
     *
     * @param exelDataRepository Репозиторий для доступа к данным обучающих занятий.
     */
    @Autowired
    public ExelDataService(ExelDataRepository exelDataRepository) {
        this.exelDataRepository = exelDataRepository;
    }

    /**
     * Выгрузка, кто прошёл на 100 баллов 65% заданий
     *
     * @return Список имен студентов, успешно прошедших более 65% заданий.
     * @throws Exception Если возникла ошибка при чтении данных.
     */
    public List<StudentDTO> findStudentsPassing65PercentTasks() throws Exception {
        List<StudentDTO> passingStudentDTOS = new ArrayList<>();
        String informationToSearch = "Виртуальная лаборатория программирования:Практическое задание";
        List<Map<String, Object>> data = exelDataRepository.readSpecificColumns(List.of(informationToSearch, "Фамилия", "Имя", "Адрес электронной почты", "Итоговая оценка за курс (Значение)"));

        for (Map<String, Object> row : data) {
            int totalTasks = 0;
            int completedTasks = 0;
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (entry.getKey().startsWith(informationToSearch) && entry.getValue() instanceof Number) {
                    totalTasks++;
                    if (((Number) entry.getValue()).doubleValue() == 100) {
                        completedTasks++;
                    }
                }
            }
            if (totalTasks > 0 && (double) completedTasks / totalTasks >= 0.65) {
                Double finalGrade;
                var finalGradeTemp = row.get("Итоговая оценка за курс (Значение)");
                if (finalGradeTemp instanceof Number) {
                    finalGrade = ((Number) finalGradeTemp).doubleValue();
                } else {
                    finalGrade = null;
                }


                passingStudentDTOS.add(
                        new StudentDTO(
                                row.get("Имя").toString(),
                                row.get("Фамилия").toString(),
                                row.get("Адрес электронной почты").toString(),
                                Optional.ofNullable(finalGrade)
                        )
                );
            }
        }
        return passingStudentDTOS;
    }

    /**
     * Кто прошёл на 65% контрольные вопросы по всем темам.
     *
     * @return Список имен студентов, успешно прошедших тест.
     * @throws Exception Если возникла ошибка при чтении данных.
     */
    public List<StudentDTO> findStudentsPassingTest() throws Exception {
        String informationToSearch = "Тест:Контрольные вопросы";
        List<Map<String, Object>> data = exelDataRepository.readSpecificColumns(List.of(informationToSearch, "Фамилия", "Имя", "Адрес электронной почты"));
        List<StudentDTO> passingStudentDTOS = new ArrayList<>();

        for (Map<String, Object> row : data) {
            int totalQuestions = 0;
            int passedQuestions = 0;
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (entry.getKey().startsWith(informationToSearch) && entry.getValue() instanceof Number) {
                    totalQuestions++;
                    if (((Number) entry.getValue()).doubleValue() == 10) {
                        passedQuestions++;
                    }
                }
            }
            if (totalQuestions > 0 && (double) passedQuestions / totalQuestions >= 0.65) {
                passingStudentDTOS.add(
                        new StudentDTO(
                                row.get("Имя").toString(),
                                row.get("Фамилия").toString(),
                                row.get("Адрес электронной почты").toString(),
                                Optional.empty()
                        )
                );
            }
        }
        return passingStudentDTOS;
    }

    /**
     * Поиск самых сильных программистов.
     *
     * @param top Количество самых сильных программистов для возврата.
     * @return Карта с именем и оценкой самых сильных программистов.
     * @throws Exception Если возникла ошибка при чтении данных.
     */
    public List<StudentDTO> mostStrongestProgrammers(int top) throws Exception {
        String informationToSearch = "Итоговая оценка за курс";
        List<Map<String, Object>> data = exelDataRepository.readSpecificColumns(List.of(informationToSearch, "Фамилия", "Имя", "Адрес электронной почты"));
        List<StudentDTO> topProgrammers = new ArrayList<>();
        for (Map<String, Object> row : data) {
            for (String columnName : row.keySet()) {
                if (columnName.startsWith(informationToSearch)) {
                    Object score = row.get(columnName);
                    if (score instanceof Number) {
                        topProgrammers.add(
                                new StudentDTO(
                                        row.get("Имя").toString(),
                                        row.get("Фамилия").toString(),
                                        row.get("Адрес электронной почты").toString(),
                                        Optional.of(
                                                Double.parseDouble(score.toString())
                                        )
                                )
                        );
                    }
                }
            }
        }

        topProgrammers.sort((o1, o2) -> {
            double num1 = o1.finalGrade().get();
            double num2 = o2.finalGrade().get();
            return Double.compare(num2, num1);
        });
        return topProgrammers.subList(0, top);
    }

    /**
     * Получение значений таблицы с хедерами между заданными индексами.
     *
     * @param start Начальный индекс строки (включительно).
     * @param end   Конечный индекс строки (включительно).
     * @return Список карт, представляющих строки таблицы с хедерами.
     */
    public List<Map<String, Object>> findInBetween(int start, int end) throws FileNotFoundException {
        return exelDataRepository.findInBetween(start, end);
    }


    public List<Map<String, Object>> findAll() throws IOException {
        return exelDataRepository.findAll();
    }
}
