package ru.spbstu.dlstats.services;

import lombok.val;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.spbstu.dlstats.data.ModeTable;

@Service
public class TableDataService {
    private final ExelDataService exelDataService;
    private final CourseService courseService;

    @Autowired
    public TableDataService(ExelDataService exelDataService, CourseService courseService) {
        this.exelDataService = exelDataService;
        this.courseService = courseService;
    }


    public Pair<String[], Object[][]> getTableData(ModeTable modeTable, Long courseId) throws Exception {
        String[] tableHeaders = null;
        Object[][] tableData = null;

        switch (modeTable) {
            case DISPLAY_GRADES:
                val allFata = exelDataService.findAll();
                tableHeaders = allFata.getFirst().keySet().toArray(String[]::new);
                tableData = allFata.stream().map(
                        rowMap -> rowMap.values().toArray(Object[]::new)
                ).toArray(Object[][]::new);
                break;
            case TASKS_65_TASKS_WITH_100_POINTS:
                val percentTasks = exelDataService.findStudentsPassing65PercentTasks();
                tableHeaders = new String[]{
                        "имя", "фамилия", "email", "общая оценка(балы)"
                };
                tableData = percentTasks.stream()
                        .map(row -> new Object[]{
                                row.name(),
                                row.lastName(),
                                row.eMail(),
                                row.finalGrade().isPresent() ? row.finalGrade().get() : "",
                        }).toArray(Object[][]::new);
                break;
            case TASKS_65_QUESTIONS_WITH_100_POINTS:
                val studentsPassingTest = exelDataService.findStudentsPassingTest();
                tableHeaders = new String[]{
                        "имя", "фамилия", "email"
                };
                tableData = studentsPassingTest.stream()
                        .map(row -> new Object[]{
                                row.name(),
                                row.lastName(),
                                row.eMail(),
                        }).toArray(Object[][]::new);
                break;
            case POWERFUL_PROGRAMMERS:
                val mostStudents = exelDataService.mostStrongestProgrammers(100);
                tableHeaders = new String[]{
                        "имя", "фамилия", "email", "общая оценка(балы)"
                };
                tableData = mostStudents.stream()
                        .map(row -> new Object[]{
                                row.name(),
                                row.lastName(),
                                row.eMail(),
                                row.finalGrade().isPresent() ? row.finalGrade().get() : "",
                        }).toArray(Object[][]::new);
                break;
            case ACTIVITY_PRACTICAL:
                val courseOp = this.courseService.findById(courseId);
                if (courseOp.isPresent()) {
                    val course = courseOp.get();
                    tableHeaders = new String[]{
                            "id студента", "имя", "email", "номер задания", "оценка", "кол. отв.", "дата ответов"
                    };
                    tableData = course.getTaskStats().stream()
                            .map(row -> new Object[]{
                                    row.getStudent().getId(),
                                    row.getStudent().getName(),
                                    row.getStudent().getEmail(),
                                    row.getTaskStatId().getTaskNum(),
                                    row.getGrade(),
                                    row.getSendAnswers(),
                                    row.getSendDate()
                            }).toArray(Object[][]::new);
                    break;
                }
                break;
            case NUM_COMPLETED_TASKS:
                tableHeaders = new String[]{
                        "номер задания", "кол. отв."
                };
                tableData = courseService.findMostAttemptedTask(courseId);
                break;
            default:
                throw new IllegalArgumentException("Неверный параметр");
        }
        return Pair.create(tableHeaders, tableData);
    }
}