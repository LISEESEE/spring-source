package com.lss;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class MyBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("yeye de method");
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println("daddy de method");
	}
	//在我们进行invokeBeanFactoryPostProcessors方法的全面讲解过程中，我们会了解到这个方法其实
	// 是为了进行我们所有的BeanFactoryPostProcessors的方法执行（BeanPostProcessors也会执行）
	//举例：MyBeanFactoryPostProcessor如果是通过实现daddy类BeanDefinitionRegistryPostProcessor，
	//优先级高，先执行这种的；daddy执行完，再去找yeye类的 直接实现BeanFactoryPostProcessor的子类

	//yeye：BeanFactoryPostProcessors优先级低
	//daddy：BeanDefinitionRegistryPostProcessor优先级高
	//daddy里边：PriorityOrdered优先级>Order优先级>啥也没有的优先级
}
