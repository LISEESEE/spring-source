package com.lss;

import org.springframework.core.convert.converter.Converter;

import java.util.List;

public class MyConvert implements Converter<List,TestBean> {
	@Override
	public TestBean convert(List source) {
		return new TestBean();
	}
}
