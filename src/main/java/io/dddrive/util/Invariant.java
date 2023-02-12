package io.dddrive.util;

import org.springframework.util.Assert;

public class Invariant {

	public static interface MessageProvider {
		String getMessage();
	}

	public static void requireThis(boolean condition, String message) {
		Assert.isTrue(condition, "Precondition failed: " + message);
	}

	public static void requireThis(boolean condition, MessageProvider messageProvider) {
		Assert.isTrue(condition, "Precondition failed: " + messageProvider.getMessage());
	}

	public static void assertThis(boolean condition, String message) {
		Assert.isTrue(condition, "Assertion failed: " + message);
	}

	public static void assertThis(boolean condition, MessageProvider messageProvider) {
		Assert.isTrue(condition, "Assertion failed: " + messageProvider.getMessage());
	}

}
