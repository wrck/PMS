package com.dp.plat.core.concurrent;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.core.task.TaskDecorator;


public class RequestThreadPoolExecutor extends ThreadPoolExecutor{
	
	private ThreadPoolExecutor threadPoolExecutor;
	private TaskDecorator taskDecorator;
	
	public RequestThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public RequestThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}

	public RequestThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}

	public RequestThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}
	
	public RequestThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor,
			TaskDecorator contextCopyingDecorator) {
		super(0, 1, 0, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		this.threadPoolExecutor = threadPoolExecutor;
		this.taskDecorator = contextCopyingDecorator;
	}

	@Override
	public void execute(Runnable command) {
		if (taskDecorator != null) {
			command = taskDecorator.decorate(command);
		}
		if (this.threadPoolExecutor != null) {
			this.threadPoolExecutor.execute(command);
		} else {
			super.execute(command);
		}
	}

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public TaskDecorator getTaskDecorator() {
        return taskDecorator;
    }

    public void setTaskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
    }

	@Override
	public void shutdown() {
		super.shutdown();
		if (this.threadPoolExecutor != null) {
			this.threadPoolExecutor.shutdown();
		}
	}
	
	
	
	@Override
	public List<Runnable> shutdownNow() {
		List<Runnable> shutdownNow = super.shutdownNow();
		if (this.threadPoolExecutor != null) {
			return this.threadPoolExecutor.shutdownNow();
		}
		return shutdownNow;
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		if (this.threadPoolExecutor != null) {
			return this.threadPoolExecutor.awaitTermination(timeout, unit);
		} else {
			return super.awaitTermination(timeout, unit);
		}
	}

	@Override
	public boolean isTerminated() {
		if (this.threadPoolExecutor != null) {
			return this.threadPoolExecutor.isTerminated();
		} else {
			return super.isTerminated();
		}
	}
	
}
