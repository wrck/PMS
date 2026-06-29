package com.dp.plat.core.concurrent;

import java.util.Map;

import org.apache.shiro.util.ThreadContext;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class ContextCopyingDecorator implements TaskDecorator {

    public Runnable decorate(Runnable runnable) {
		final String parentThreadName = Thread.currentThread().getName();
		// 拷贝请求上下文
		RequestAttributes context = null;
		try {
			context = RequestContextHolder.currentRequestAttributes();
		} catch (IllegalStateException e) {
		}
		// 拷贝请求上下文
		RequestAttributes requestContext = context;
//		// 拷贝Spring安全上下文
//		SecurityContext securityContext = SecurityContextHolder.getContext();
		// 拷贝shiro上下文
		Map<Object, Object> resources = ThreadContext.getResources();
//		System.out.println("【" + Thread.currentThread().getName() + "】1:" + UserContext.getCurrentUser());
		return () -> {
            final String currentThreadName = Thread.currentThread().getName();
            try {
//                  System.out.println("【" + Thread.currentThread().getName() + "】2:" + UserContext.getCurrentUser());
                RequestContextHolder.setRequestAttributes(requestContext, true);
//                SecurityContextHolder.setContext(securityContext);
                ThreadContext.setResources(resources);
//                  System.out.println("【" + Thread.currentThread().getName() + "】3:" + UserContext.getCurrentUser());
                runnable.run();
//                  System.out.println("【" + Thread.currentThread().getName() + "】4:" + UserContext.getCurrentUser());
            } finally {
                if (!parentThreadName.equals(currentThreadName)) {
                    ThreadContext.remove();
//                        SecurityContextHolder.clearContext();
                    RequestContextHolder.resetRequestAttributes();
//                      System.out.println("【" + Thread.currentThread().getName() + "】5:" + UserContext.getCurrentUser());
                }
            }
        };
	}
}