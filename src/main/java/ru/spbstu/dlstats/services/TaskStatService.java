package ru.spbstu.dlstats.services;


import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.spbstu.dlstats.dto.combinations.TaskStatStudentCombo;
import ru.spbstu.dlstats.models.TaskStat;
import ru.spbstu.dlstats.models.TaskStatId;
import ru.spbstu.dlstats.repositorys.TaskStatRepository;
import ru.spbstu.dlstats.specifications.TaskStatSpecifications;

import java.util.List;
import java.util.Optional;

@Service
public class TaskStatService {
    private final TaskStatRepository taskStatRepository;


    public TaskStatService(TaskStatRepository taskStatRepository) {
        this.taskStatRepository = taskStatRepository;
    }


    public TaskStat save(TaskStat taskStat) {
        return this.taskStatRepository.save(taskStat);
    }


    public void saveAll(Iterable<TaskStat> taskStats) {
        this.taskStatRepository.saveAll(taskStats);
    }

    public List<TaskStat> findAll(Specification<TaskStat> specification, Sort sort) {
        return taskStatRepository.findAll(specification, sort);
    }

    public List<TaskStat> findAll(Specification<TaskStat> specification) {
        return this.taskStatRepository.findAll(specification);
    }

    public Optional<TaskStat> findById(TaskStatId id) {
        return this.taskStatRepository.findById(id);
    }


    public List<TaskStat> findDataFilter(TaskStatStudentCombo statStudentCombo) {
        return this.taskStatRepository.findAll(TaskStatSpecifications.withStudentTaskStatCombo(statStudentCombo));
    }

    public List<TaskStat> findTasksWithMaxAttemptsForCourse(Long courseId) {
        return taskStatRepository.findTasksWithMaxAttemptsForCourse(courseId);
    }

    public long countByTaskStatId(TaskStatId taskStatId) {
        return taskStatRepository.countByTaskStatId(taskStatId);
    }

    public boolean existsByTaskStatId(TaskStatId taskStatId) {
        return taskStatRepository.existsByTaskStatId(taskStatId);
    }

    public Optional<TaskStat> findByTaskStatId(TaskStatId taskStatId) {
        return taskStatRepository.findById(taskStatId);
    }
}
