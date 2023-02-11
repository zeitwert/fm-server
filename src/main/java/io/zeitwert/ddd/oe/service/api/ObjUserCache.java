package io.zeitwert.ddd.oe.service.api;

import java.time.OffsetDateTime;
import java.util.Optional;

import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.ddd.oe.model.ObjUser;

public interface ObjUserCache extends AggregateCache<ObjUser> {

	Optional<ObjUser> getByEmail(String email);

	OffsetDateTime touch(Integer userId);

	OffsetDateTime getLastTouch(Integer userId);

}
