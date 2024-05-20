package ru.spbstu.dlstats.utils;

import java.io.File;

public class FileFinder {
    private final File downloadDirectory;
    private final String nameFile;

    public FileFinder(String directoryPath, String nameFile) {
        this.downloadDirectory = new File(directoryPath);
        this.nameFile = nameFile;
    }

    public File findFile() {
        // Проверяем, что директория существует и является директорией
        if (downloadDirectory.exists() && downloadDirectory.isDirectory()) {
            // Получаем список файлов внутри директории
            File[] files = downloadDirectory.listFiles();

            // Проходимся по списку файлов и ищем файл по имени
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().equals(nameFile)) {
                        // Файл найден, возвращаем объект File
                        return file;
                    }
                }
            }
        }
        // Файл не найден или директория недоступна
        return null;
    }
}
