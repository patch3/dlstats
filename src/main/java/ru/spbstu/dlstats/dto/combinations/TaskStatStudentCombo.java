package ru.spbstu.dlstats.dto.combinations;


import lombok.Value;

@Value
public class TaskStatStudentCombo {
    String nameStudent;
    String emailStudent;
    Float taskNum;
    Integer sendAnswers;
    Float grade;
}
