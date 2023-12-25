package com.lss;

import com.lss.event.MyEvent;
import com.lss.lifecycle.MyLifeCycle;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.ArrayList;

public class BeanTest {
	public static void main(String[] args) {
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
//		System.out.println(context.getBean(TestBean.class).getName());
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		TestBean bean = (TestBean)context.getBean("testBean");
		System.out.println(bean.getName());

		context.publishEvent(new MyEvent("","myself"));
		//我们的spring的事件的发布和监听执行，其实就是我们的观察者模式。
		//caster：事件的发布器，它里边有一个castEvent方法；他还有一个listener list，知道把事件发给谁 event
		//listener 当我们的listener收到事件之后，就会调用我们的onApplicationEvent方法，进行event方法的执行

		//他是一个spring类型的转化器的总容器。比如说，我现在需要将我们的list格式的数据转化
		//为TestBean的格式。证明我们可以依托于Conversion进行我们需要转化的各种数据类型
		DefaultConversionService conversionService = (DefaultConversionService)DefaultConversionService.getSharedInstance();
		conversionService.addConverter(new MyConvert());
		TestBean convert = conversionService.convert(new ArrayList<>(), TestBean.class);
		System.out.println(convert.getName());

		//lifecycle专门处理我们的lifecycle的子类。包含start和stop方法，可以由我们自由控制当前的
		//lifecycle子类的生命周期。如果是TestBean这种，没有实现lifecycle，我们没有办法实时控制
		MyLifeCycle cycle = (MyLifeCycle)context.getBean("myLifeCycle");
		System.out.println(cycle.isRunning());//false
		cycle.start();
		System.out.println(cycle.isRunning());//true
		cycle.stop();
		System.out.println(cycle.isRunning());//false
	}
}
