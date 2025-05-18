package com.onion.backend.config;


import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


@Slf4j
@Configuration
@EnableAsync(proxyTargetClass = true)
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncUncaughtExceptionHandler();
    }

    @Bean(name = "elasticsearch-async")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        // 큐의 용량(capacity) -> 시간 제한이 걸리지 않고 큐에서 무기한 대기하는 것이 기본 동작
        executor.setThreadNamePrefix("es-async-");
        return executor;
    }

    @Slf4j
    private static class CustomAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

        @Override
        public void handleUncaughtException(Throwable ex, Method method, Object... params) {
            String paramsString = Arrays.stream(params).reduce("", (p1, p2) -> {
                StringBuffer sb = new StringBuffer();
                sb.append(p1).append(", ").append(p2.toString());
                return sb.toString();
            }).toString();
            log.error("method name={}  parameters={}  AsyncException message={}", method.getName(), paramsString,
                    ex.getMessage());
        }
    }

    // 비동기에서 context가 필요한 경우 세팅을 위해서 만들어 놓음
    private static class AsyncContextCopyDecorator implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable runnable) {
            SecurityContext context = SecurityContextHolder.getContext();

            return () -> {
                try {
                    SecurityContextHolder.setContext(context);
                    runnable.run();
                } finally {
                    SecurityContextHolder.clearContext();
                }
            };

        }
    }
}
