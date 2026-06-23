package com.healthcare.query.config;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

class AsyncConfigurationTest {

    @Test
    void taskExecutor_ShouldCreateConfiguredExecutor() {
        AsyncConfiguration configuration = new AsyncConfiguration();
        Executor executor = configuration.taskExecutor();

        assertNotNull(executor);
        assertTrue(executor instanceof ThreadPoolTaskExecutor);

        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
        assertEquals(5, taskExecutor.getCorePoolSize());
        assertEquals(10, taskExecutor.getMaxPoolSize());
        assertEquals(100, taskExecutor.getQueueCapacity());
        assertTrue(taskExecutor.getThreadNamePrefix().startsWith("async-"));
    }

    @Test
    void taskExecutor_ShouldBeInitialized() {
        AsyncConfiguration configuration = new AsyncConfiguration();
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) configuration.taskExecutor();

        assertNotNull(executor);
        assertEquals(5, executor.getCorePoolSize());
    }
}
