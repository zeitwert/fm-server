package io.zeitwert.ddd.util;

import org.springframework.util.Assert;

public class Check {

	public static void requireThis(boolean condition, String message) {
		Assert.isTrue(condition, "Precondition failed: " + message);
	}

	public static void assertThis(boolean condition, String message) {
		Assert.isTrue(condition, "Assertion failed: " + message);
	}

}
