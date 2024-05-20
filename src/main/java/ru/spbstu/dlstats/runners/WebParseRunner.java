package ru.spbstu.dlstats.runners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.spbstu.dlstats.services.managers.WebParseServiceManager;

@Slf4j
@Component
@Order(2)
public class WebParseRunner implements CommandLineRunner {
    private final WebParseServiceManager webParseServiceManager;

    public WebParseRunner(WebParseServiceManager webParseServiceManager) {
        this.webParseServiceManager = webParseServiceManager;
    }

    @Override
    public void run(String... args) {
        this.webParseServiceManager.updateData();
    }
}
