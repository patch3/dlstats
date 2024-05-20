package ru.spbstu.dlstats.configs;

import lombok.val;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DriverConfig {
    private static final String DOWNLOAD_FOLDER_NAME = "data";

    @Value("${parse.driver}")
    private String driver;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ChromeDriver chromeDriver() {
        val options = new ChromeOptions();
        options.addArguments("--headless=new"); // Запуск в режиме "головного" браузера
        options.addArguments("--disable-gpu"); // Отключение GPU
        options.addArguments("--window-size=1280,720"); // Размер окна браузера
        options.addArguments("--disable-extensions"); // Отключение расширений
        options.addArguments("--no-sandbox"); // Отключение песочницы
        options.addArguments("--disable-dev-shm-usage"); // Отключение /dev/shm

        setDownloadDirectory(options);

        // Инициализация WebDriver с настройками
        return new ChromeDriver(options);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public FirefoxDriver firefoxDriver() {
        val options = new FirefoxOptions();
        options.addArguments("--headless"); // Запуск в режиме "головного" браузера
        options.addArguments("-width=1280"); // Ширина окна браузера
        options.addArguments("-height=720"); // Высота окна браузера

        setDownloadDirectory(options);

        // Инициализация WebDriver с настройками
        return new FirefoxDriver(options);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public WebDriver webDriver() {
        if ("chrome".equalsIgnoreCase(driver)) {
            return chromeDriver();
        } else if ("firefox".equalsIgnoreCase(driver)) {
            return firefoxDriver();
        } else {
            throw new IllegalArgumentException("Unsupported driver: " + driver);
        }
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public File downloadDirectory() {
        // Получите путь к папке напротив программы
        String currentDir = System.getProperty("user.dir");
        String downloadDir = currentDir + File.separator + DOWNLOAD_FOLDER_NAME;
        return new File(downloadDir);
    }

    private void setDownloadDirectory(ChromeOptions options) {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", this.downloadDirectory().getPath());
        options.setExperimentalOption("prefs", prefs);
    }

    private void setDownloadDirectory(FirefoxOptions options) {
        options.addPreference("browser.download.folderList", 2);
        options.addPreference("browser.download.dir", this.downloadDirectory().getPath());
        options.addPreference("browser.helperApps.neverAsk.saveToDisk",
                "application/octet-stream,application/vnd.ms-excel");
    }


}
