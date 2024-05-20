package ru.spbstu.dlstats.repositorys;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.dlstats.models.Student;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @EntityGraph(attributePaths = {"taskStats.student", "taskStats.course"})
    Optional<Student> findById(Long id);

}