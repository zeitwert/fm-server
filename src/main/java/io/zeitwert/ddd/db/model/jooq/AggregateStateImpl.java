package io.zeitwert.ddd.db.model.jooq;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.db.model.AggregateState;

public class AggregateStateImpl implements AggregateState {

	private final UpdatableRecord<?> baseRecord;
	private final UpdatableRecord<?> extnRecord;

	public AggregateStateImpl(UpdatableRecord<?> baseRecord, UpdatableRecord<?> extnRecord) {
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
