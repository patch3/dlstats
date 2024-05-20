package ru.spbstu.dlstats.controllers.staff;

import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.spbstu.dlstats.data.ModeTable;
import ru.spbstu.dlstats.services.TableDataService;

import java.util.Objects;

@Controller
@RequestMapping("/staff/stats")
public final class StatsController {
    public static final ModeTable DEFAULT_MODE_TABLE = ModeTable.DISPLAY_GRADES;

    private final TableDataService tableDataService;

    @Autowired
    public StatsController(TableDataService tableDataService) {
        this.tableDataService = tableDataService;
    }


    @GetMapping("/{id}")
    public String basePage(
            @PathVariable("id") Long courseId,
            @RequestParam(value = "modeTable", required = false) ModeTable modeTable,
            @RequestParam(value = "action", required = false) String action,
            Model model) {
        modeTable = Objects.requireNonNullElse(modeTable, DEFAULT_MODE_TABLE);
        action = Objects.requireNonNullElse(action, "update");
        switch (action) {
            case "export" -> {
                return "redirect:/staff/stats/export/" + courseId + "?modeTable=" + modeTable;
            }
            case "update" -> {
                model.addAttribute("title", "dl-stat");
                model.addAttribute("id", courseId);
                model.addAttribute("modeTables", ModeTable.values());
                model.addAttribute("selectedValue", modeTable);

                Pair<String[], Object[][]> pairHeaderAndData;
                try {
                    pairHeaderAndData = tableDataService.getTableData(modeTable, courseId);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    model.addAttribute("errorMassage", "Не удалось получить данные");
                    return "/error";
                }
                model.addAttribute("tableHeaders", pairHeaderAndData.getFirst());
                model.addAttribute("tableData", pairHeaderAndData.getSecond());
                return "/staff/table/stat";
            }
            default -> {
                model.addAttribute("errorMassage", "не верный параметр");
                return "/error";
            }
        }

    }
}
