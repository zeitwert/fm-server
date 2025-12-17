package io.dddrive.core.ddd.model.enums;

import io.dddrive.core.enums.model.Enumerated;
import io.dddrive.core.enums.model.Enumeration;

public final class CodePartListType implements Enumerated {

	private final String id;
	private final String name;

	public CodePartListType(Enumeration<? extends Enumerated> enumeration, String id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public CodePartListTypeEnum getEnumeration() {
		return CodePartListTypeEnum.getInstance();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

}
