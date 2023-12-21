package com.lss;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanTest {
	public static void main(String[] args) {
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
//		System.out.println(context.getBean(TestBean.class).getName());
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		TestBean bean = (TestBean)context.getBean("testBean");
		System.out.println(bean.getName());
	}
}
