package io.zeitwert.ddd.oe.service.api.impl;

import io.zeitwert.ddd.app.event.AggregateStoredEvent;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.service.api.UserService;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {

	private final ObjUserRepository userRepository;
	private final Map<Integer, OffsetDateTime> objTouchMap = new HashMap<>();
	private final Cache<Integer, ObjUser> objCache = Caffeine.newBuilder().maximumSize(100).recordStats().build();
	private final Cache<Integer, EnumeratedDto> enumCache = Caffeine.newBuilder().maximumSize(100).recordStats().build();

	private int objCacheClear = 0;
	private int enumCacheClear = 0;

	public UserServiceImpl(ObjUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public ObjUser getUser(Integer userId) {
		if (userId == null) {
			return null;
		}
		ObjUser user = this.objCache.getIfPresent(userId);
		if (user == null) {
			user = this.userRepository.get(userId);
			this.objCache.put(userId, user);
		}
		return user;
	}

	public Optional<ObjUser> getByEmail(String email) {
		Optional<ObjUser> maybeUser = this.userRepository.getByEmail(email);
		if (maybeUser.isPresent()) {
			ObjUser user = maybeUser.get();
			this.objCache.put(user.getId(), user);
		}
		return maybeUser;
	}

	public EnumeratedDto getUserEnumerated(Integer userId) {
		if (userId == null) {
			return null;
		}
		EnumeratedDto user = this.enumCache.getIfPresent(userId);
		if (user == null) {
			user = ObjUserDtoAdapter.getInstance().asEnumerated(this.getUser(userId));
			this.enumCache.put(userId, user);
		}
		return user;
	}

	public OffsetDateTime touch(Integer userId) {
		OffsetDateTime timestamp = OffsetDateTime.now();
		this.objTouchMap.put(userId, timestamp);
		return timestamp;
	}

	public OffsetDateTime getLastTouch(Integer userId) {
		return this.objTouchMap.get(userId);
	}

	public Map<String, Integer> getStatistics() {
		CacheStats objStats = this.objCache.stats();
		CacheStats enumStats = this.enumCache.stats();
		return Map.of(
				"objCacheHit", (int) objStats.hitCount(),
				"objCacheMiss", (int) objStats.missCount(),
				"objCacheClear", this.objCacheClear,
				"enumCacheHit", (int) enumStats.hitCount(),
				"enumCacheMiss", (int) enumStats.missCount(),
				"enumCacheClear", this.enumCacheClear);
	}

	@EventListener
	public void handleAggregateStoredEvent(AggregateStoredEvent event) {
		Integer id = event.getAggregate().getId();
		if (this.objCache.getIfPresent(id) != null) {
			this.objCacheClear += 1;
			this.objCache.invalidate(id);
		}
		if (this.enumCache.getIfPresent(id) != null) {
			this.enumCacheClear += 1;
			this.enumCache.invalidate(id);
		}
	}

}
