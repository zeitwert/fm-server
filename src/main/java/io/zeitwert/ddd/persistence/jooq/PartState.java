package io.zeitwert.ddd.persistence.jooq;

import org.jooq.UpdatableRecord;

public record PartState(UpdatableRecord<?> dbRecord) {
}
