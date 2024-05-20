package ru.spbstu.dlstats.services.managers;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.spbstu.dlstats.configs.CourseConfig;
import ru.spbstu.dlstats.services.parse.ParseService;

import java.util.Set;

@Slf4j
@Service
public class WebParseServiceManager {
    private final CourseConfig courseConfig;

    private final ApplicationContext applicationContext;

    @Autowired
    public WebParseServiceManager(CourseConfig courseConfig,
                                  ApplicationContext applicationContext) {
        this.courseConfig = courseConfig;
        this.applicationContext = applicationContext;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateData() {
        var parseService = this.applicationContext.getBean(ParseService.class);
        try {
            parseService.authenticationSystem();
            for (var courseId : courseConfig.getCourseIds()) {
                Set<Pair<Long, Float>> pairSetTasksAndTaskNum;
                try {
                    pairSetTasksAndTaskNum = parseService.parseTasksIdByCourseId(courseId);
                } catch (NoSuchElementException ex) {
                    log.error("Course №{}: {}", courseId, ex.getMessage(), ex);
                    continue;
                }
                for (var taskPair : pairSetTasksAndTaskNum) {
                    try {
                        parseService.parseStatsForTaskById(taskPair.getFirst(), taskPair.getSecond(), courseId);
                    } catch (RuntimeException ex) {
                        log.error("Table №{}: {}", taskPair, ex.getMessage(), ex);
                    }
                }
            }

        } catch (Exception ex) {
            log.error("Error during data update", ex);
        } finally {
            parseService.close();
        }
    }
}
