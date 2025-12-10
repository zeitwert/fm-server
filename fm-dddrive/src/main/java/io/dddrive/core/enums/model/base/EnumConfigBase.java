package io.dddrive.core.enums.model.base;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class EnumConfigBase {

	private static final AtomicInteger configCounter = new AtomicInteger(0);
	private static final Set<EnumerationBase<?>> enumerations = new HashSet<>();

	protected static boolean isInConfig() {
		return configCounter.get() > 0;
	}

	static void addEnum(EnumerationBase<?> enumeration) {
		enumerations.add(enumeration);
	}

	static void assignEnumItems() {
		for (EnumerationBase<?> enumeration : enumerations) {
			enumeration.assignItems();
		}
		enumerations.clear();
	}

	protected void startConfig() {
		configCounter.incrementAndGet();
	}

	protected void endConfig() {
		configCounter.getAndUpdate(counter -> {
			if (counter == 1) {
				assignEnumItems();
			}
			return counter - 1;
		});
	}

}
