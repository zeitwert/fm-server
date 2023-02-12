package io.dddrive.oe.service.api;

import java.time.OffsetDateTime;
import java.util.Optional;

import io.dddrive.ddd.service.api.AggregateCache;
import io.dddrive.oe.model.ObjUser;

public interface ObjUserCache extends AggregateCache<ObjUser> {

	Optional<ObjUser> getByEmail(String email);

	OffsetDateTime touch(Integer userId);

	OffsetDateTime getLastTouch(Integer userId);

}
