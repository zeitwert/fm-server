package io.zeitwert.ddd.db.model.jooq;

import org.jooq.UpdatableRecord;

public class AggregateState {

	private final UpdatableRecord<?> baseRecord;
	private final UpdatableRecord<?> extnRecord;

	public AggregateState(UpdatableRecord<?> baseRecord, UpdatableRecord<?> extnRecord) {
		this.baseRecord = baseRecord;
		this.extnRecord = extnRecord;
	}

	public UpdatableRecord<?> getBaseRecord() {
		return this.baseRecord;
	}

	public UpdatableRecord<?> getExtnRecord() {
		return this.extnRecord;
	}

}
