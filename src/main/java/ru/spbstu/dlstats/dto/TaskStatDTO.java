package ru.spbstu.dlstats.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class TaskStatDTO {
    private Long studentId;
    private Float taskNum;
    private Integer sendAnswers;
    private Float grade;
    private LocalDateTime sendDate;

    public void setSendDateTimeFromString(String dateTimeString) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive() // Игнорировать регистр букв
                .appendPattern("EEEE, d MMMM yyyy, HH:mm") // Паттерн для парсинга даты и времени
                .toFormatter(Locale.forLanguageTag("ru")); // Установка русской локали

        this.sendDate = LocalDateTime.parse(dateTimeString, formatter);
    }
}
