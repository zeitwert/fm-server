package io.dddrive.jooq.ddd;

import org.jooq.UpdatableRecord;

public record PartState(UpdatableRecord<?> dbRecord) {

	static public final String BASE = "base";

}
