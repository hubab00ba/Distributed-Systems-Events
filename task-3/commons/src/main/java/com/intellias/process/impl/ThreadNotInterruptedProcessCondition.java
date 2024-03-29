package com.intellias.process.impl;

import com.intellias.process.ProcessCondition;

public class ThreadNotInterruptedProcessCondition implements ProcessCondition {
	@Override
	public boolean shouldRun() {
		return !Thread.currentThread().isInterrupted();
	}
}
