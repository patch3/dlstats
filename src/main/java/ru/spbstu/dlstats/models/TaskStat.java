package ru.spbstu.dlstats.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "taskstat")
public class TaskStat {
    @EmbeddedId
    private TaskStatId taskStatId;

    @Column(name = "send_answers", nullable = false)
    private Integer sendAnswers; // количество отправленных ответов(попытки)

    @Column(name = "grade", columnDefinition = "DECIMAL(5, 2)")
    private Float grade;

    @Column(name = "send_date", nullable = false)
    private LocalDateTime sendDate;

    @ManyToOne
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;


    public void setSendDateTimeFromString(String dateTimeString) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive() // Игнорировать регистр букв
                .appendPattern("EEEE, d MMMM yyyy, HH:mm") // Паттерн для парсинга даты и времени
                .toFormatter(Locale.forLanguageTag("ru")); // Установка русской локали

        this.sendDate = LocalDateTime.parse(dateTimeString, formatter);
    }
}