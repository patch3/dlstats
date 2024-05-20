package ru.spbstu.dlstats.services;


import org.springframework.stereotype.Service;
import ru.spbstu.dlstats.models.Student;
import ru.spbstu.dlstats.repositorys.StudentRepository;
import ru.spbstu.dlstats.services.parse.ParseService;

import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    public Student findById(Long studentId, ParseService parseService) {
        return this.studentRepository.findById(studentId)
                .orElseGet(() -> this.studentRepository.save(parseService.parseStudent(studentId)));
    }

    public Optional<Student> findById(Long studentId) {
        return this.studentRepository.findById(studentId);
    }
}
