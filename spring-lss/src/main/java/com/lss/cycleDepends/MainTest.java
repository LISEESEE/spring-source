package com.lss.cycleDepends;

import com.lss.cycleDepends.proxy.JDKProxyBeanPostProcessor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MainTest {

	private static Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

	//定义一个一级缓存，这个缓存保存我们的bean对象，避免多次创建
	private static Map<String,Object> singletonObjects = new ConcurrentHashMap<>();
	//如果只有一个singletonObjects一级缓存，至少存在两个问题：
	//1、singletonObjects里边存放的是classA和classB的不完整对象，如果这时候有其他线程进行getbean的调用，
	//会得到不完整的对象。singletonObjects最终里边会存储两种对象，一种是没有循环依赖的完整对象；另一种就是
	// 有循环依赖的的不完整对象。
	//2、singletonObjects如果仅仅存储我们的完整对象，不存储不完整对象，那么程序就无法运行。
	//引入二级缓存：存储不完整对象
	private static Map<String,Object> earlySingletonObjects = new ConcurrentHashMap<>();

	//现在，我们发现如果存在两个或两个类以上的循环依赖问题，并且这些类中的一个或多个存在方法增强AOP，会出现一些问题。
	//这些问题就是我们之前说的，如果一个类有aop，直接使用二级缓存会导致每次拿到的都是最初的那个instance，我们应该
	//期望拿到每次动态代理生成的类，然后现实中，spring中的生命周期顺序是：实例化——属性填充（进行每次循环依赖的属性填充）
	//——初始化——aop（aop每次可能生成不同的代理对象，可是由于aop的位置太靠后了，没办法干预到属性填充，就导致属性填充过程
	// 中，没法用到aop生成的新的代理类，导致依赖注入的错误）。所以我们需要引入一个三级缓存，这个三级缓存需要有实力将我们
	//的aop操作移动到实例化之后，属性填充之前。所以，我们的三级缓存需要有 延迟加载的功能。（钩子，函数式编程，回调方法）
	private static Map<String, ObjectFactory<Object>> singletonFactories = new ConcurrentHashMap<>();

	//当前循环依赖的bean是否正在创建中
	private static Set<String> singletonCurrentlyInCreation = new HashSet<>();

	public static void loadBeanDefinitions(){
		RootBeanDefinition abd = new RootBeanDefinition(ClassA.class);
		RootBeanDefinition bbd = new RootBeanDefinition(ClassB.class);
		beanDefinitionMap.put("classA",abd);
		beanDefinitionMap.put("classB",bbd);
	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		loadBeanDefinitions();
		for (String beanName : beanDefinitionMap.keySet()){
			getBean(beanName);
		}
		ClassA a =(ClassA) getBean("classA");
		a.execute();
	}

	private static Object getBean(String beanName) throws InstantiationException, IllegalAccessException {
		Object singleton = getSingleton(beanName);
		if (singleton != null){
			return singleton;
		}

		if (!singletonCurrentlyInCreation.contains(beanName)){
			singletonCurrentlyInCreation.add(beanName);
		}

		//第一步 实例化：进行我们bean的实例化
		RootBeanDefinition bd = (RootBeanDefinition) beanDefinitionMap.get(beanName);
		Class<?> clazz = bd.getBeanClass();
		//调用无参构造
		Object instance = clazz.newInstance();
		//存储了不完整对象
		earlySingletonObjects.put(beanName,instance);

		//如果有循环依赖并且有aop代理，必须将classA的方法增强挪到这里。第四步：方法增强AOP
		//（延迟添加）
		if (singletonCurrentlyInCreation.contains(beanName)){
			singletonFactories.put(beanName,
					() -> new JDKProxyBeanPostProcessor()
							.getEarlyBeanReference(earlySingletonObjects.get(beanName),beanName));
		}



		//第二步：属性填充
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Autowired annotation = field.getAnnotation(Autowired.class);
			if (annotation != null) {
				field.setAccessible(true);
				Object dep = getBean(field.getName());
				field.set(instance,dep);
			}
		}
		//第三步：初始化（调用一些init-method）


		//添加到缓存
		//刚开始spring认为，这个缓存已经足够，因为每个对象创建完成后都会得到
		//一个完整的对象，可以放到一个缓存中，但是没有考虑一些问题
		singletonObjects.put(beanName,instance);
		earlySingletonObjects.remove(beanName);
		singletonFactories.remove(beanName);
		return instance;
	}

	private static Object getSingleton(String beanName) {
		Object singleton = singletonObjects.get(beanName);
		//如果一级缓存里没有
		if (singleton == null && singletonCurrentlyInCreation.contains(beanName)){
			singleton = earlySingletonObjects.get(beanName);
			if (singleton == null){
				ObjectFactory<Object> factory = singletonFactories.get(beanName);
				if (factory != null){
					singleton = factory.getObject();
					//之所以有这句话就是之前咋们说了半天的，aop生成的代理对象，
					//需要替换二级缓存中初始生成的那个对象
					earlySingletonObjects.put(beanName,singleton);
					singletonFactories.remove(beanName);
				}
			}
		}
		return singleton;
	}
}
