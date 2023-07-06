package cloud.framework.async;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步配置类
 *
 * @author xmc
 */
@EnableAsync
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        int coreNumber = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(coreNumber);
        taskExecutor.setMaxPoolSize(200);
        taskExecutor.setQueueCapacity(25);
        taskExecutor.setKeepAliveSeconds(200);
        taskExecutor.setThreadNamePrefix("cloud-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationMillis(60);
        taskExecutor.initialize();
        return taskExecutor;
    }

}