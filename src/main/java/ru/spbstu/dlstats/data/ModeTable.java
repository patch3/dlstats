package ru.spbstu.dlstats.data;

import lombok.Getter;

@Getter
public enum ModeTable {
    DISPLAY_GRADES("Отображать оценки c портала"),
    ACTIVITY_PRACTICAL("Отображать активность сдачи практических заданий"),
    TASKS_65_TASKS_WITH_100_POINTS("Кто прошёл на 100 баллов 65% заданий?"),
    TASKS_65_QUESTIONS_WITH_100_POINTS("Кто прошёл на 65% контрольные вопросы по всем темам."),
    POWERFUL_PROGRAMMERS("Самые сильные программисты"),
    NUM_COMPLETED_TASKS("Задания, которое проходили с наибольшим количеством попыток.");


    private final String displayText;

    ModeTable(String displayText) {
        this.displayText = displayText;
    }
}
