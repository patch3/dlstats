package ru.spbstu.dlstats.repositorys;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.spbstu.dlstats.models.Course;

import java.util.Optional;


@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @EntityGraph(attributePaths = {"taskStats.student", "taskStats.course"})
    Optional<Course> findById(Long id);


    @Query("SELECT ts.taskStatId.taskNum AS taskNum, SUM(ts.sendAnswers) AS totalAnswers " +
            "FROM Course c " +
            "JOIN c.taskStats ts " +
            "WHERE c.id = :courseId " +
            "GROUP BY ts.taskStatId.taskNum " +
            "ORDER BY totalAnswers DESC")
    Object[][] findMostAttemptedTask(@Param("courseId") Long courseId);
}