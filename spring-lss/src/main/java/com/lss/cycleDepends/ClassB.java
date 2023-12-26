package com.lss.cycleDepends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClassB {
	@Autowired
	private ClassA classA;

	public ClassB(ClassA classA) {
		this.classA = classA;
	}
	public ClassB() {
		System.out.println("classB init success!");
	}

	public ClassA getClassA() {
		return classA;
	}

	public void setClassA(ClassA classA) {
		this.classA = classA;
	}
}
