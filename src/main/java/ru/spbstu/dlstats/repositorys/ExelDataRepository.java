package ru.spbstu.dlstats.repositorys;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.spbstu.dlstats.utils.FileFinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Setter
@AllArgsConstructor
@Component
public class ExelDataRepository {
    @Value("${parse.course.export.exel.name.file}")
    private String nameFile;

    private File downloadDirectory;


    @Autowired
    public ExelDataRepository(File downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
    }

    public File exelFile() throws FileNotFoundException {
        FileFinder fileFinder = new FileFinder(downloadDirectory.getPath(), nameFile);
        val file = fileFinder.findFile();
        if (file == null) {
            throw new FileNotFoundException("File not found in the directory: " + nameFile);
        }
        return file;
    }


    /**
     * Чтение определенных столбцов, начинающихся с заданных строк.
     *
     * @param startsWith Список строк, с которых должны начинаться имена столбцов.
     * @return Список карт, представляющих строки таблицы с выбранными столбцами.
     * @throws IOException Если возникает ошибка при чтении файла.
     */
    public List<Map<String, Object>> readSpecificColumns(List<String> startsWith) throws IOException {
        File table = exelFile();
        List<Map<String, Object>> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(table);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            Map<String, Integer> columnIndexMap = new HashMap<>();
            for (Cell cell : headerRow) {
                String columnName = cell.getStringCellValue();
                for (String prefix : startsWith) {
                    if (columnName.startsWith(prefix)) {
                        columnIndexMap.put(columnName, cell.getColumnIndex());
                    }
                }
            }
            iterateRows(data, sheet, columnIndexMap);
        }
        return data;
    }

    /**
     * Чтение конкретной ячейки по имени столбца и индексу строки.
     *
     * @param columnName Имя столбца.
     * @param rowIndex   Индекс строки.
     * @return Значение ячейки.
     * @throws IOException Если возникает ошибка при чтении файла.
     */
    public Object readSpecificCell(String columnName, int rowIndex) throws IOException {
        File table = exelFile();
        try (FileInputStream fis = new FileInputStream(table);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            // Находим индекс столбца по имени
            int columnIndex = -1;
            for (Cell cell : headerRow) {
                if (cell.getStringCellValue().equals(columnName)) {
                    columnIndex = cell.getColumnIndex();
                    break;
                }
            }

            if (columnIndex == -1) {
                throw new IllegalArgumentException("Column not found: " + columnName);
            }

            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                throw new IllegalArgumentException("Row not found at index: " + rowIndex);
            }

            Cell cell = row.getCell(columnIndex);
            if (cell == null) {
                throw new IllegalArgumentException("Cell not found at column: " + columnName + " and row: " + rowIndex);
            }

            return getCellValue(cell);
        }
    }

    /**
     * Чтение всех данных из таблицы.
     *
     * @return Список карт, представляющих все строки таблицы.
     * @throws IOException Если возникает ошибка при чтении файла.
     */
    public List<Map<String, Object>> findAll() throws IOException {
        File table = exelFile();
        List<Map<String, Object>> data = new LinkedList<>();

        try (FileInputStream fis = new FileInputStream(table);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            Map<String, Integer> columnIndexMap = new LinkedHashMap<>();
            for (Cell cell : headerRow) {
                String columnName = cell.getStringCellValue();
                columnIndexMap.put(columnName, cell.getColumnIndex());
            }
            iterateRows(data, sheet, columnIndexMap);
        }
        return data;
    }

    private void iterateRows(List<Map<String, Object>> data, Sheet sheet, Map<String, Integer> columnIndexMap) {
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }

            prepareMap(data, columnIndexMap, row);
        }
    }

    /**
     * Получение значения ячейки.
     *
     * @param cell Ячейка таблицы.
     * @return Значение ячейки.
     */
    private Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * Поиск данных между заданными индексами.
     *
     * @param start Начальный индекс строки (включительно).
     * @param end   Конечный индекс строки (включительно).
     * @return Список карт, представляющих строки таблицы между заданными индексами.
     */
    public List<Map<String, Object>> findInBetween(int start, int end) throws FileNotFoundException {
        File table = exelFile();
        List<Map<String, Object>> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(table);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            Map<String, Integer> columnIndexMap = new LinkedHashMap<>();
            for (Cell cell : headerRow) {
                String columnName = cell.getStringCellValue();
                columnIndexMap.put(columnName, cell.getColumnIndex());
            }

            for (int rowNum = start; rowNum <= end; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    row = sheet.createRow(rowNum);
                }
                prepareMap(data, columnIndexMap, row);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    /**
     * Подготовка карты данных для строки.
     *
     * @param data           Список карт для добавления данных.
     * @param columnIndexMap Карта индексов столбцов.
     * @param row            Строка таблицы для чтения данных.
     */
    private void prepareMap(List<Map<String, Object>> data, Map<String, Integer> columnIndexMap, Row row) {
        Map<String, Object> rowData = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : columnIndexMap.entrySet()) {
            Cell cell = row.getCell(entry.getValue());
            if (cell != null) {
                rowData.put(entry.getKey(), getCellValue(cell));
            }
        }
        if (!rowData.isEmpty()) {
            data.add(rowData);
        }
    }
}
