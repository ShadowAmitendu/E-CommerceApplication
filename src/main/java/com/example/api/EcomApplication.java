package com.example.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the E-Commerce Spring Boot application.
 *
 * <p>What's happening here:
 * This class boots up the entire Spring framework environment. The {@link SpringBootApplication}
 * annotation activates auto-configuration, component scanning across the {@code com.example.api}
 * package hierarchy, and configuration property bindings.
 *
 * <p>What is done:
 * <ul>
 *   <li>Initializes the Spring Application Context.</li>
 *   <li>Starts the embedded web server (e.g., Tomcat) on the configured port.</li>
 *   <li>Scans and registers beans, repositories, services, and REST controllers into the container.</li>
 * </ul>
 */
@SpringBootApplication
public class EcomApplication {

    /**
     * Main method that serves as the entry point for the Java application execution.
     *
     * @param args Command-line arguments passed to the application at launch.
     */
    public static void main(String[] args) {
        // Launches the Spring Boot application using reflection and initializes all beans
        SpringApplication.run(EcomApplication.class, args);
    }

}
