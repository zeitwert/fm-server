package io.zeitwert.ddd.oe.service.api.impl;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.zeitwert.ddd.aggregate.service.api.base.AggregateCacheBase;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;

@Service("userCache")
public class ObjUserCacheImpl extends AggregateCacheBase<ObjUser> implements ObjUserCache {

	private final Cache<String, Integer> userCacheByEmail = Caffeine.newBuilder().maximumSize(100).recordStats().build();
	private final Map<Integer, OffsetDateTime> objTouchMap = new HashMap<>();

	public ObjUserCacheImpl(ObjUserRepository userRepository) {
		super(userRepository, ObjUser.class);
	}

	public Optional<ObjUser> getByEmail(String email) {
		Integer userId = this.userCacheByEmail.get(email, (id) -> this.getFromEmail(email));
		return Optional.of(this.get(userId));
	}

	private Integer getFromEmail(String email) {
		Optional<ObjUser> maybeUser = ((ObjUserRepository) this.getRepository()).getByEmail(email);
		if (!maybeUser.isPresent()) {
			return null;
		}
		return maybeUser.get().getId();
	}

	public OffsetDateTime touch(Integer userId) {
		OffsetDateTime timestamp = OffsetDateTime.now();
		this.objTouchMap.put(userId, timestamp);
		return timestamp;
	}

	public OffsetDateTime getLastTouch(Integer userId) {
		return this.objTouchMap.get(userId);
	}

}
