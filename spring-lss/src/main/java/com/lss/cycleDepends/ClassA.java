package com.lss.cycleDepends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClassA implements IClassA{
	@Autowired
	private ClassB classB;

	public ClassA(ClassB classB) {
		this.classB = classB;
	}
	public ClassA() {
		System.out.println("classA init success!");
	}
	public ClassB getClassB() {
		return classB;
	}

	public void setClassB(ClassB classB) {
		this.classB = classB;
	}

	@Override
	public void execute() {
		System.out.println("execute classA");
	}
}
