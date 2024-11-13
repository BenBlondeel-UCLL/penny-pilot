package edu.troy.pennypilot.ui;

import edu.troy.pennypilot.SpringApplication;
import javafx.application.Application;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class SpringInitTask extends Task<ConfigurableApplicationContext> implements ApplicationListener {

    private final Application.Parameters parameters;

    @Override
    protected ConfigurableApplicationContext call() throws Exception {
        return new SpringApplicationBuilder(SpringApplication.class)
                .web(WebApplicationType.NONE)
                .listeners(this)
                .run(parameters.getRaw().toArray(new String[0]));
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        try {
            switch (event) {
                case ApplicationContextInitializedEvent e -> {
                    updateMessage("Initializing Spring");
                    updateProgress(10, 100);
                }
                case ApplicationPreparedEvent e -> {
                    updateMessage("Bootstrapping JPA");
                    updateProgress(20, 100);
                }
                case ApplicationStartedEvent e -> {
                    updateMessage("Database scripts running");
                    updateProgress(60, 100);
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                case ApplicationReadyEvent e -> {
                    updateMessage("Application ready");
                    updateProgress(99, 100);
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                default -> {}
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
