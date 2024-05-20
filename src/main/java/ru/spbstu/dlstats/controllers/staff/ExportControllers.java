package ru.spbstu.dlstats.controllers.staff;

import lombok.val;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.spbstu.dlstats.data.ModeTable;
import ru.spbstu.dlstats.services.TableDataService;
import ru.spbstu.dlstats.utils.ExelTableBuilder;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Optional;

@Controller
@RequestMapping("/staff/stats/export")
public class ExportControllers {
    private final TableDataService tableDataService;

    @Autowired
    public ExportControllers(TableDataService tableDataService) {
        this.tableDataService = tableDataService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> exportExel(@PathVariable("id") Long courseId,
                                               @RequestParam(value = "modeTable") ModeTable modeTable) {
        Pair<String[], Object[][]> pairHeaderAndData;
        try {
            pairHeaderAndData = tableDataService.getTableData(modeTable, courseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        try {
            val builderExel = new ExelTableBuilder(modeTable.getDisplayText(), "Статистика");
            for (var i = 0; i < pairHeaderAndData.getFirst().length; ++i) {
                val ic = i;
                builderExel.addColumn(
                        pairHeaderAndData.getFirst()[i],
                        Arrays.stream(pairHeaderAndData.getSecond())
                                .map(row ->
                                        Optional.ofNullable(row[ic]).map(Object::toString)
                                                .orElse("")
                                ).toList()
                );
            }

            val baos = new ByteArrayOutputStream();
            builderExel.write(baos);
            val data = baos.toByteArray();
            val resource = new ByteArrayResource(data);

            val headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + modeTable + ".xlsx");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(data.length)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resource);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

    }
}

