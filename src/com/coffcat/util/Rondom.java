package com.coffcat.util;

import org.junit.Test;

public class Rondom {
	public static int getRandNum(int min, int max) {
		int randNum = min + (int) (Math.random() * ((max - min) + 1));
		return randNum;
	}

	@Test
	public void rand() {
		System.out.println("随机数为" + getRandNum(100000, 999999));
	}
}