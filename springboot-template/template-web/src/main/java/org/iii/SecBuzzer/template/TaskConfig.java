package org.iii.SecBuzzer.template;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;

@Configuration
public class TaskConfig {

	@Bean("mailTaskExecutor")
	public Executor mailTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		taskExecutor.setAwaitTerminationSeconds(60);
		taskExecutor.setCorePoolSize(4);
		taskExecutor.setMaxPoolSize(8);
		taskExecutor.setQueueCapacity(Integer.MAX_VALUE);
		taskExecutor.setKeepAliveSeconds(60);
		taskExecutor.setThreadNamePrefix("mail-thread-");
		return taskExecutor;
	}
}