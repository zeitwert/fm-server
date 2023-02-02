package io.zeitwert.ddd.db.model.jooq;

import org.jooq.UpdatableRecord;

public record AggregateState(UpdatableRecord<?> baseRecord, UpdatableRecord<?> extnRecord) {
}
