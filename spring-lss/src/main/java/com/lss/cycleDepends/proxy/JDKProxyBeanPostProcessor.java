package com.lss.cycleDepends.proxy;

import com.lss.cycleDepends.ClassA;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class JDKProxyBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {
	public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
		if (bean instanceof ClassA){
			JDKDynamicProxy jdkDynamicProxy = new JDKDynamicProxy(bean);
			return jdkDynamicProxy.getProxy();
		}
		return bean;
	}
}
