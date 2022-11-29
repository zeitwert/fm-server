package io.zeitwert.ddd.aggregate.service.api.base;

import java.util.Map;

import org.springframework.context.event.EventListener;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.ddd.app.event.AggregateStoredEvent;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;

public abstract class AggregateCacheBase<A extends Aggregate> implements AggregateCache<A> {

	private final AggregateRepository<A, ?> repository;
	private final Cache<Integer, A> objCache = Caffeine.newBuilder().maximumSize(100).recordStats().build();
	private final Cache<Integer, EnumeratedDto> enumCache = Caffeine.newBuilder().maximumSize(100).recordStats().build();

	private int objCacheClear = 0;
	private int enumCacheClear = 0;

	public AggregateCacheBase(AggregateRepository<A, ?> repository, Class<A> aggregateClass) {
		this.repository = repository;
		AppContext.getInstance().addCache(aggregateClass, this);
	}

	protected AggregateRepository<A, ?> getRepository() {
		return this.repository;
	}

	public A get(Integer id) {
		if (id == null) {
			return null;
		}
		A aggregate = this.objCache.get(id, (aggrId) -> this.repository.get(aggrId));
		return aggregate;
	}

	public EnumeratedDto getAsEnumerated(Integer id) {
		if (id == null) {
			return null;
		}
		EnumeratedDto dto = this.enumCache.get(id, (aggrId) -> this.getEnumeratedDto(aggrId));
		return dto;
	}

	private EnumeratedDto getEnumeratedDto(Integer id) {
		A aggregate = this.get(id);
		return EnumeratedDto.builder()
				.id("" + aggregate.getId())
				.itemType(EnumeratedDto.fromEnum(this.repository.getAggregateType()))
				.name(aggregate.getCaption())
				.build();
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
