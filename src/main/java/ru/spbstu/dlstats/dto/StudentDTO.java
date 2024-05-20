package ru.spbstu.dlstats.dto;

import java.util.Optional;

public record StudentDTO(
        String name,
        String lastName,
        String eMail,
        Optional<Double> finalGrade
) {
}
