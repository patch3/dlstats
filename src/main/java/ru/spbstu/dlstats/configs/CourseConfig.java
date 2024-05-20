package ru.spbstu.dlstats.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class CourseConfig {
    @Value("${parse.course.tasks.stats.url}")
    private String taskStatUrl;

    @Value("${parse.course.tasks.url}")
    private String tasksUrl;

    @Value("${parse.course.course.url}")
    private String courseUrl;

    @Value("${parse.course.ids}")
    private Long[] courseIds;

    @Value("${parse.course.student.url}")
    private String studentUrl;

    @Value("${parse.course.export.exel.url}")
    private String urlExportExel;


    @Value("${parse.course.export.exel.name.file}")
    private String ExelNameFile;

}
