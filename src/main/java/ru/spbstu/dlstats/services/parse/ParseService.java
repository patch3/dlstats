package ru.spbstu.dlstats.services.parse;


import lombok.val;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.spbstu.dlstats.configs.AuthConfig;
import ru.spbstu.dlstats.configs.CourseConfig;
import ru.spbstu.dlstats.models.*;
import ru.spbstu.dlstats.services.CourseService;
import ru.spbstu.dlstats.services.StudentService;
import ru.spbstu.dlstats.services.TaskStatService;

import javax.naming.AuthenticationException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Scope("prototype")
public class ParseService {
    public static final int SLEEP_TIME = 10000;

    private final ParseTaskHashService parseTaskHashService;
    private final TaskStatService taskStatService;
    private final StudentService studentService;

    private final WebDriver webDriver;

    private final AuthConfig authConfig;
    private final CourseConfig courseConfig;

    private final WebDriverWait wait;
    private final CourseService courseService;

    @Autowired
    public ParseService(
            ParseTaskHashService parseTaskHashService,
            TaskStatService taskStatService,
            StudentService studentService,
            WebDriver webDriver,
            AuthConfig authConfig,
            CourseConfig courseConfig, CourseService courseService) {
        this.parseTaskHashService = parseTaskHashService;
        this.taskStatService = taskStatService;
        this.studentService = studentService;
        this.webDriver = webDriver;
        this.authConfig = authConfig;
        this.courseConfig = courseConfig;

        this.wait = new WebDriverWait(webDriver, Duration.ofSeconds(1));

        System.out.println("-----start parse!-----");
        this.courseService = courseService;
    }

    public void close() {
        if (webDriver != null)
            webDriver.quit();
        System.out.println("-----close parse!-----");
    }

    public boolean authenticate(String username, String password) {
        webDriver.get(authConfig.getAuthUrl());

        // Ждем, пока элемент станет кликабельным
        WebElement socialButton = webDriver.findElement(By.className("auth0-lock-social-button-text"));
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript("arguments[0].click();", socialButton);


        wait.until(ExpectedConditions.elementToBeClickable(By.id("user"))).sendKeys(username);
        //webDriver.findElement(By.id("user")).sendKeys(username);
        webDriver.findElement(By.id("password")).sendKeys(password);
        webDriver.findElement(By.id("doLogin")).click();

        try {
            return webDriver.findElements(By.className("form_alert")).isEmpty();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void authenticationSystem() throws AuthenticationException {
        if (!this.authenticate(authConfig.getUsername(), authConfig.getPassword())) {
            throw new AuthenticationException("Не удалось авторизоваться");
        }
    }

    public void parseStatsForTaskById(Long taskId, Float taskNum, Long courseId) {
        webDriver.get(String.format("%s?id=%s", courseConfig.getTaskStatUrl(), taskId));

        var symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator(',');
        var decimalFormat = new DecimalFormat("###.###", symbols);
        decimalFormat.setParseBigDecimal(false);

        try {
            var table = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("generaltable")));

            val tableStr = table.getText();
            // проверка а нужноли парсить вообще, или данные не обновились?
            val existingParse = this.parseTaskHashService.findById(taskId);
            ParseTaskHash existingParseDTO;
            if (existingParse.isPresent()) {
                existingParseDTO = existingParse.get();
                if (existingParseDTO.getTaskHash().equals(ParseTaskHash.calculateHash(tableStr))) return;
                existingParseDTO.setTaskHash(tableStr);
            } else {
                existingParseDTO = new ParseTaskHash();
                existingParseDTO.setId(taskId);
            }

            val patternIdStudent = Pattern.compile("id=(\\d+)");
            var rows = table.findElements(By.tagName("tr"));

            var taskStatDTOs = rows.stream()
                    .skip(1) // Пропускаем заголовок таблицы
                    .parallel() // Включаем параллельную обработку
                    .map(row -> {
                        val cells = row.findElements(By.tagName("td"));
                        val taskstatId = new TaskStatId();
                        taskstatId.setCourseId(courseId);

                        // Избегаем лишних вызовов findElement
                        val userLinkElement = cells.get(2).findElement(By.tagName("a"));
                        val userLink = userLinkElement.getAttribute("href");

                        val matcherIdStudent = patternIdStudent.matcher(userLink);
                        if (!matcherIdStudent.find()) {
                            throw new RuntimeException("Не удалось распарсить id студента");
                        }
                        String studentId = matcherIdStudent.group(1);

                        taskstatId.setStudentId(Long.parseLong(studentId));

                        taskstatId.setTaskNum(taskNum);

                        // Парсим дату и время отправки
                        WebElement sendDateTimeCell = cells.get(3).findElement(By.tagName("a"));
                        val sendDateTime = sendDateTimeCell.getText();

                        // Парсим количество отправленных ответов
                        var sendAnswersCell = cells.get(4).findElement(By.tagName("a"));
                        val sendAnswers = sendAnswersCell.getText();

                        // Парсим оценку
                        var gradeText = cells.get(5).findElement(By.tagName("div")).getText();

                        Float grade;
                        if (gradeText.equals("Без оценки")) {
                            grade = null;
                        } else {
                            var gradeParts = gradeText.split("/");
                            try {
                                grade = decimalFormat.parse(gradeParts[0].trim()).floatValue();
                            } catch (Exception e) {
                                e.printStackTrace();
                                grade = null;
                            }
                        }
                        TaskStat taskStat = new TaskStat();

                        taskStat.setTaskStatId(taskstatId);
                        taskStat.setSendDateTimeFromString(sendDateTime);
                        taskStat.setSendAnswers(Integer.parseInt(sendAnswers));
                        taskStat.setGrade(grade);

                        return taskStat;
                    }).toArray(TaskStat[]::new);

            for (TaskStat taskStat : taskStatDTOs) {
                taskStat.setCourse(courseService.findById(taskStat.getTaskStatId().getCourseId(), this));
                taskStat.setStudent(studentService.findById(taskStat.getTaskStatId().getStudentId(), this));

                // Проверяем, существует ли запись с таким же TaskStatId
                Optional<TaskStat> existingTaskStat = taskStatService.findByTaskStatId(taskStat.getTaskStatId());
                if (existingTaskStat.isPresent()) {
                    // Обновляем существующую запись
                    TaskStat existing = existingTaskStat.get();
                    existing.setSendDate(taskStat.getSendDate());
                    existing.setSendAnswers(taskStat.getSendAnswers());
                    existing.setGrade(taskStat.getGrade());
                    taskStatService.save(existing);
                } else {
                    taskStatService.save(taskStat);
                }
            }
            this.parseTaskHashService.save(existingParseDTO);
        } catch (NoSuchElementException ex) {
            // Передаем исключение дальше для обработки вызывающим кодом
            ex.printStackTrace();
            throw new RuntimeException("Ошибка при парсинге таблицы", ex);
        }
    }

    public Student parseStudent(Long studentId) {
        webDriver.get(
                String.format(
                        "%s?id=%s",
                        courseConfig.getStudentUrl(),
                        studentId
                )
        );
        return Student.builder()
                .id(studentId)
                .name(webDriver.findElement(By.className("page-title")).getText())
                .email(webDriver.findElement(
                        By.xpath("//section[@class='node_category card d-inline-block w-100 mb-3']//a[starts-with(@href, 'mailto:')]")
                ).getText())
                .build();
    }

    public Course parseCourse(Long idCourse) {
        this.webDriver.get(
                String.format(
                        "%s?id=%s",
                        courseConfig.getCourseUrl(),
                        idCourse
                )
        );
        return new Course(
                idCourse,
                this.webDriver.findElement(
                        By.cssSelector("li.breadcrumb-item:last-child > a")
                ).getText()
        );
    }

    public Set<Pair<Long, Float>> parseTasksIdByCourseId(Long courseId) {
        String url = String.format("%s?id=%s", this.courseConfig.getTasksUrl(), courseId);
        webDriver.get(url);

        By graderTableLocator = By.className("gradereport-grader-table");
        By headingLocator = By.className("heading");
        By gradeItemHeaderLocator = By.className("gradeitemheader");

        Pattern pattern = Pattern.compile("id=(\\d+)");
        Pattern taskNumPattern = Pattern.compile("-?\\d+(?:\\.\\d+)?");

        wait.until(ExpectedConditions.presenceOfElementLocated(graderTableLocator));

        return webDriver.findElements(graderTableLocator)
                .stream()
                .parallel()
                .flatMap(table -> table.findElements(headingLocator).stream())
                .flatMap(heading -> heading.findElements(gradeItemHeaderLocator).stream())
                .filter(webElement -> {
                    String href = webElement.getAttribute("href");
                    return href != null && href.contains("https://dl-ido.spbstu.ru/mod/vpl/");
                })
                .map(webElement -> {
                    String href = webElement.getAttribute("href");
                    Matcher matcher = pattern.matcher(href);
                    if (matcher.find()) {
                        String id = matcher.group(1);
                        String title = webElement.getAttribute("title");

                        Float taskNum = null;

                        Matcher taskNumMatcher = taskNumPattern.matcher(title);
                        if (taskNumMatcher.find()) {
                            String numberString = taskNumMatcher.group();
                            taskNum = Float.valueOf(numberString);
                        }

                        assert taskNum != null;
                        return Pair.of(Long.parseLong(id), taskNum);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public void downloadFile(Long courseId) throws InterruptedException {
        webDriver.get(this.courseConfig.getUrlExportExel() + "?id=" + courseId);
        webDriver.findElement(By.id("id_submitbutton")).click();
        Thread.sleep(SLEEP_TIME);
    }
}