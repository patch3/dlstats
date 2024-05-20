package ru.spbstu.dlstats.utils;

import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Getter
public class ExelTableBuilder {
    private final Workbook workbook;

    private final String tableName;

    private final Sheet sheet;

    /**
     * Конструктор класса TableBuilder.
     *
     * @param tableName Имя таблицы (без расширения).
     * @param sheetName Имя листа.
     */
    public ExelTableBuilder(String tableName, String sheetName) {
        this.tableName = tableName;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet(sheetName);
    }

    /**
     * Создание заголовка ячейки.
     *
     * @param name        Название заголовка.
     * @param columnIndex Индекс столбца.
     */
    private void createHeaderCell(String name, int columnIndex) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            headerRow = sheet.createRow(0);
        }
        Cell headerCell = headerRow.createCell(columnIndex);
        headerCell.setCellValue(name);
    }

    /**
     * Создание строк данных.
     *
     * @param data          Список данных.
     * @param columnIndex   Индекс столбца.
     * @param startRowIndex Начальный индекс строки.
     */
    private void createDataRows(List<String> data, int columnIndex, int startRowIndex) {
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.getRow(startRowIndex + i);
            if (row == null) {
                row = sheet.createRow(startRowIndex + i);
            }
            Cell cell = row.createCell(columnIndex);
            if (data.get(i).contains("=")) {
                cell.setCellValue(data.get(i).split("=")[0]);
            } else {
                cell.setCellValue(data.get(i));
            }
        }
    }

    /**
     * Создание строк данных из карты.
     *
     * @param data          Карта данных.
     * @param columnIndex   Индекс столбца.
     * @param startRowIndex Начальный индекс строки.
     */
    private void createDataRows(Map<String, Double> data, int columnIndex, int startRowIndex) {
        int rowIndex = startRowIndex;
        for (Map.Entry<String, ?> entry : data.entrySet()) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            Cell keyCell = row.createCell(columnIndex);
            if (entry.getKey().contains("=")) {
                keyCell.setCellValue(entry.getKey().split("=")[0]);
            } else {
                keyCell.setCellValue(entry.getKey());
            }

            Cell valueCell = row.createCell(columnIndex + 1);

            valueCell.setCellValue(entry.getValue().toString());

            rowIndex++;
        }
    }

    /**
     * Добавление столбца с данными.
     *
     * @param name Название столбца.
     * @param data Список данных.
     */
    public ExelTableBuilder addColumn(String name, List<String> data) {
        int columnIndex = sheet.getRow(0) == null ? 0 : sheet.getRow(0).getLastCellNum();
        createHeaderCell(name, columnIndex);
        createDataRows(data, columnIndex, 1);
        return this;
    }

    /**
     * Сохранение таблицы в файл XLSX.
     *
     * @throws IOException Если возникает ошибка при записи файла.
     */
    public void saveToXLSX() throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(tableName + ".xlsx")) {
            workbook.write(outputStream);
        }
        workbook.close();
    }

    public void write(OutputStream outputStream) {
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null)
                    workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
