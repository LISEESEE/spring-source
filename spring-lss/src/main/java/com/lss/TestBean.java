package com.lss;

import org.springframework.stereotype.Component;

@Component
public class TestBean {
	private String name = "lss is me!";

	public TestBean(String name) {
		this.name = name;
	}
	public TestBean() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
