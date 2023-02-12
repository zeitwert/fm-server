package io.dddrive.jooq.ddd;

import org.jooq.UpdatableRecord;

public record AggregateState(UpdatableRecord<?> baseRecord, UpdatableRecord<?> extnRecord) {

	static public final String BASE = "base";
	static public final String EXTN = "extn";

}
