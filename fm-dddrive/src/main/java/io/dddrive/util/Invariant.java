package io.dddrive.util;

public class Invariant {

	public interface MessageProvider {
		String getMessage();
	}

	public static void requireThis(boolean condition, String message) {
		if (!condition) throw new RuntimeException("Precondition failed: " + message);
	}

	public static void requireThis(boolean condition, MessageProvider messageProvider) {
		if (condition) { // don't evaluate messageProvider if condition is true
			return;
		}
		throw new RuntimeException("Precondition failed: " + messageProvider.getMessage());
	}

	public static void assertThis(boolean condition, String message) {
		if (!condition) throw new RuntimeException("Assertion failed: " + message);
	}

	public static void assertThis(boolean condition, MessageProvider messageProvider) {
		if (condition) { // don't evaluate messageProvider if condition is true
			return;
		}
		throw new RuntimeException("Assertion failed: " + messageProvider.getMessage());
	}

}
