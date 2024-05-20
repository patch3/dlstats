package ru.spbstu.dlstats.repositorys;


import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.spbstu.dlstats.models.TaskStat;
import ru.spbstu.dlstats.models.TaskStatId;

import java.util.List;

@Repository
public interface TaskStatRepository extends JpaRepository<TaskStat, TaskStatId>, JpaSpecificationExecutor<TaskStat> {
    List<TaskStat> findAll();

    List<TaskStat> findAll(Specification specificity);

    List<TaskStat> findAll(Specification specificity, Sort sport);

    @Query("SELECT ts FROM TaskStat ts " +
            "WHERE ts.course.id = :courseId AND ts.sendAnswers = " +
            "(SELECT MAX(ts2.sendAnswers) FROM TaskStat ts2 " +
            "WHERE ts2.course.id = :courseId)")
    List<TaskStat> findTasksWithMaxAttemptsForCourse(@Param("courseId") Long courseId);

    long countByTaskStatId(TaskStatId taskStatId);

    boolean existsByTaskStatId(TaskStatId taskStatId);
}
