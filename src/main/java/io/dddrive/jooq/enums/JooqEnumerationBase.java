package io.dddrive.jooq.enums;

import io.dddrive.app.service.api.impl.Enumerations;
import io.dddrive.enums.model.Enumerated;
import io.dddrive.enums.model.base.EnumerationBase;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

public abstract class JooqEnumerationBase<E extends Enumerated> extends EnumerationBase<E> {

	static protected final Field<String> ID = DSL.field("id", String.class);
	static protected final Field<String> NAME = DSL.field("name", String.class);

	private final DSLContext dslContext;

	public JooqEnumerationBase(Class<E> enumeratedClass, Enumerations enums, DSLContext dslContext) {
		super(enumeratedClass, enums);
		this.dslContext = dslContext;
	}

	protected DSLContext getDslContext() {
		return this.dslContext;
	}

}
