package io.dddrive.jooq.util;

import org.jooq.Field;
import org.jooq.impl.DSL;

public class CustomDSL {

	public static Field<Long> length(Field<byte[]> field) {
		return DSL.field("length({0})", Long.class, field);
	}

}
