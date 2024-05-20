package ru.spbstu.dlstats.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.dlstats.models.Course;
import ru.spbstu.dlstats.repositorys.CourseRepository;
import ru.spbstu.dlstats.services.parse.ParseService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    @Value("${parse.course.ids}")
    private Long[] courseIds;


    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> findAll() {
        return this.courseRepository.findAll();
    }

    public void update(ParseService parse) {
        var courses = Arrays.stream(courseIds)
                .map(parse::parseCourse)
                .collect(Collectors.toSet());
        this.courseRepository.saveAll(courses);
    }

    @Transactional
    public Course findById(Long id, ParseService parseService) {
        return courseRepository.findById(id)
                .orElseGet(() -> courseRepository.save(parseService.parseCourse(id)));
    }

    public Optional<Course> findById(Long id) {
        return this.courseRepository.findById(id);
    }

    public Object[][] findMostAttemptedTask(Long courseId) {
        return this.courseRepository.findMostAttemptedTask(courseId);
    }

    public boolean existsById(Long id) {
        return this.courseRepository.existsById(id);
    }
}