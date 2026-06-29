package com.dp.plat.activiti.process.ServiceTask;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import com.dp.plat.activiti.entity.ExaminedPerson;

/**
 * 如果用ServiceTask的activiti:class 属性执行此类，注解的service都为空
 * 因为每次执行都会为JavaDelegate创建新的实例，也就是每次都new 一个新的。 这样@Autowired
 * 就不能自动注入，必须让Spring容器管理此类，而不是每次都new一个 这样@Autowired
 * 才会自动注入。所以，用@Component注解，此注解会让JavaDelegate实现类实例化到spring容器中，
 * 达到让Spring容器管理此类的目的，并且bpmn的ServiceTask属性用activiti:delegateExpression=
 * "${beanId}" 这样 就可以解决调用service为空的问题。
 * 
 * @author w02611
 *
 */
public class PerformanceObjective implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		ExaminedPerson examinedPerson = (ExaminedPerson) execution.getVariable("examinedPerson");
		System.out.println(examinedPerson.getUserId() + "," + examinedPerson.getUserName() + ","
				+ examinedPerson.getSupervisorId());
	}

}
