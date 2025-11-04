package ys.cafe.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsyncConfig {

    /**
     * I/O 작업 위주의 비동기 작업을 위한 스레드 풀 설정
     *
     * I/O 작업 특성:
     * - 네트워크 호출, DB 쿼리, 파일 I/O 등에서 대기 시간이 많음
     * - CPU를 실제로 사용하는 시간은 짧음
     * - 따라서 CPU 코어 수보다 훨씬 많은 스레드가 효율적
     *
     * 스레드 수 공식: CPU 코어 수 × (1 + 대기시간/계산시간)
     * - 대기시간 90%, 계산시간 10%인 경우: 코어 수 × 10
     */
    @Bean(name = "asyncExecutor")
    public ExecutorService executorService() {
        int availableProcessorsCount = Runtime.getRuntime().availableProcessors();

        // I/O 작업 위주 설정
        // 대기시간이 계산시간의 약 10~20배라고 가정
        int coreThreadCount = availableProcessorsCount * 10;   // 기본 스레드 수
        int maxThreadCount = availableProcessorsCount * 20;    // 최대 스레드 수
        int queueCapacity = 500;  // 대기 큐 크기 (I/O 작업은 큐를 크게)
        int keepAliveSeconds = 60; // 유휴 스레드 유지 시간

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        // 스레드 풀 크기 설정
        threadPoolTaskExecutor.setCorePoolSize(coreThreadCount);
        threadPoolTaskExecutor.setMaxPoolSize(maxThreadCount);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);

        // 유휴 스레드 타임아웃 설정 (core 스레드도 타임아웃 적용)
        threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);

        // 스레드 이름 접두사 (디버깅/모니터링 용이)
        threadPoolTaskExecutor.setThreadNamePrefix("async-io-");

        // 거부 정책: 호출자 스레드에서 실행 (CallerRunsPolicy)
        // 큐가 가득 차면 요청한 스레드에서 직접 실행
        threadPoolTaskExecutor.setRejectedExecutionHandler(
            new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy()
        );

        threadPoolTaskExecutor.initialize();

        return threadPoolTaskExecutor.getThreadPoolExecutor();
    }
}
