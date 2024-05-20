package ru.spbstu.dlstats.specifications;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.spbstu.dlstats.dto.combinations.TaskStatStudentCombo;
import ru.spbstu.dlstats.models.TaskStat;

import java.util.ArrayList;
import java.util.List;

public class TaskStatSpecifications {

    public static Specification<TaskStat> withStudentTaskStatCombo(TaskStatStudentCombo studentTaskStatCombo) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Добавляем условия фильтрации на основе выбранного поля
            if (!(studentTaskStatCombo.getNameStudent() == null || studentTaskStatCombo.getNameStudent().isEmpty())) {
                predicates.add(criteriaBuilder.equal(root.get("student").get("name"), studentTaskStatCombo.getNameStudent()));
            }
            if (!(studentTaskStatCombo.getEmailStudent() == null || studentTaskStatCombo.getEmailStudent().isEmpty())) {
                predicates.add(criteriaBuilder.equal(root.get("student").get("email"), studentTaskStatCombo.getEmailStudent()));
            }
            if (studentTaskStatCombo.getTaskNum() != null) {
                predicates.add(criteriaBuilder.equal(root.get("taskNum"), studentTaskStatCombo.getTaskNum()));
            }
            if (studentTaskStatCombo.getSendAnswers() != null) {
                predicates.add(criteriaBuilder.equal(root.get("sendAnswers"), studentTaskStatCombo.getSendAnswers()));
            }
            if (studentTaskStatCombo.getGrade() != null) {
                predicates.add(criteriaBuilder.equal(root.get("grade"), studentTaskStatCombo.getGrade()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
