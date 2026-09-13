package com.example.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test class for the Spring Boot application.
 *
 * <p>What's happening here:
 * This test suite utilizes {@link SpringBootTest} to bootstrap the complete Spring application
 * context in a test environment.
 *
 * <p>What is done:
 * <ul>
 *   <li>Loads and verifies all Spring bean configurations, components, services, and repositories.</li>
 *   <li>Ensures there are no dependency injection cycles, missing beans, or configuration errors.</li>
 * </ul>
 */
@SpringBootTest
class EcomApplicationTests {

    /**
     * Sanity check test case to ensure that the Spring application context loads without failure.
     *
     * <p>If any bean fails to initialize or database configuration is invalid during startup,
     * this test will fail immediately.
     */
    @Test
    void contextLoads() {
        // Method body is intentionally empty. The test passes if the Spring context loads successfully.
    }

}
