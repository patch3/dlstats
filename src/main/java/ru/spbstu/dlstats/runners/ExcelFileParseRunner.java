package ru.spbstu.dlstats.runners;

import lombok.val;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.spbstu.dlstats.configs.CourseConfig;
import ru.spbstu.dlstats.services.parse.ParseService;
import ru.spbstu.dlstats.utils.FileFinder;

import javax.naming.AuthenticationException;
import java.io.File;

@Component
@Order(1)
public class ExcelFileParseRunner implements CommandLineRunner {
    private final ApplicationContext applicationContext;

    private final CourseConfig courseConfig;

    private final File downloadDirectory;


    public ExcelFileParseRunner(ApplicationContext applicationContext, CourseConfig courseConfig, File downloadDirectory) {
        this.applicationContext = applicationContext;
        this.courseConfig = courseConfig;
        this.downloadDirectory = downloadDirectory;
    }

    @Override
    public void run(String... args) throws Exception {
        parseExcelFile();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void parseExcelFile() throws AuthenticationException, InterruptedException {
        val parseService = applicationContext.getBean(ParseService.class);
        val courseId = courseConfig.getCourseIds()[0];

        FileFinder fileFinder = new FileFinder(downloadDirectory.getPath(), courseConfig.getExelNameFile());
        val file = fileFinder.findFile();
        if (file != null && !file.delete()) {
            throw new SecurityException("Не удалось удалить файл: " + file.getAbsolutePath());
        }

        parseService.authenticationSystem();
        parseService.downloadFile(courseId);
        parseService.close();
    }
}
