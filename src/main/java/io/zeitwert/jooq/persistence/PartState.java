package io.zeitwert.jooq.persistence;

import org.jooq.UpdatableRecord;

public record PartState(UpdatableRecord<?> dbRecord) {

	static public final String BASE = "base";

}
