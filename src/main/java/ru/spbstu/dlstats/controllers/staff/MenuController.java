package ru.spbstu.dlstats.controllers.staff;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.spbstu.dlstats.configs.CourseConfig;
import ru.spbstu.dlstats.services.CourseService;

@Controller
@RequestMapping("/staff/menu")
public class MenuController {
    private final CourseService courseService;
    private final CourseConfig courseConfig;

    public MenuController(CourseService courseService, CourseConfig courseConfig) {
        this.courseService = courseService;
        this.courseConfig = courseConfig;
    }

    @GetMapping
    public String initializeBasePage(Model model) {
        model.addAttribute("courses", this.courseService.findAll());
        model.addAttribute("tasksStatsUrl", this.courseConfig.getTasksUrl());
        model.addAttribute("title", "dl-stat");
        return "/staff/menu";
    }


}
