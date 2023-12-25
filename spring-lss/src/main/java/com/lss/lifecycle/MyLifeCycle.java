package com.lss.lifecycle;

import org.springframework.context.Lifecycle;
import org.springframework.stereotype.Component;

@Component
public class MyLifeCycle implements Lifecycle {
	private boolean running;
	@Override
	public void start() {
		System.out.println("my lifecycle start!");
		this.running = true;
	}

	@Override
	public void stop() {
		System.out.println("my lifecycle stop!");
		this.running = false;
	}

	@Override
	public boolean isRunning() {
		System.out.println("my lifecycle isRunning!");
		return this.running;
	}
}
