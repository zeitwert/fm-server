package io.zeitwert.fm.oe.service.api.impl;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.dddrive.ddd.service.api.base.AggregateCacheBase;
import io.dddrive.oe.model.ObjUser;
import io.dddrive.oe.service.api.ObjUserCache;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;

@Service("userCache")
public class ObjUserCacheImpl extends AggregateCacheBase<ObjUser> implements ObjUserCache {

	private final Cache<String, Integer> userCacheByEmail = Caffeine.newBuilder().maximumSize(100).recordStats().build();
	private final Map<Integer, OffsetDateTime> objTouchMap = new HashMap<>();

	public ObjUserCacheImpl(ObjUserFMRepository userRepository) {
		super(userRepository, ObjUser.class);
	}

	@Override
	public Optional<ObjUser> getByEmail(String email) {
		Integer userId = this.userCacheByEmail.get(email, (id) -> this.getFromEmail(email));
		return Optional.of(this.get(userId));
	}

	private Integer getFromEmail(String email) {
		Optional<ObjUserFM> maybeUser = ((ObjUserFMRepository) this.getRepository()).getByEmail(email);
		if (!maybeUser.isPresent()) {
			return null;
		}
		return maybeUser.get().getId();
	}

	@Override
	public OffsetDateTime touch(Integer userId) {
		OffsetDateTime timestamp = OffsetDateTime.now();
		this.objTouchMap.put(userId, timestamp);
		return timestamp;
	}

	@Override
	public OffsetDateTime getLastTouch(Integer userId) {
		return this.objTouchMap.get(userId);
	}

}
