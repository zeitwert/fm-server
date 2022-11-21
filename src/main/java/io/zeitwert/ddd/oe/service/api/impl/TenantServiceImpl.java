package io.zeitwert.ddd.oe.service.api.impl;

import io.zeitwert.ddd.app.event.AggregateStoredEvent;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.service.api.TenantService;

import java.util.Map;
import java.util.Optional;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service("tenantService")
public class TenantServiceImpl implements TenantService {

	private final ObjTenantRepository tenantRepository;
	private final Cache<Integer, ObjTenant> objCache = Caffeine.newBuilder().maximumSize(100).recordStats().build();
	private final Cache<Integer, EnumeratedDto> enumCache = Caffeine.newBuilder().maximumSize(100).recordStats().build();

	private int objCacheClear = 0;
	private int enumCacheClear = 0;

	public TenantServiceImpl(ObjTenantRepository tenantRepository) {
		this.tenantRepository = tenantRepository;
	}

	public ObjTenant getTenant(Integer tenantId) {
		if (tenantId == null) {
			return null;
		}
		ObjTenant tenant = this.objCache.getIfPresent(tenantId);
		if (tenant == null) {
			tenant = this.tenantRepository.get(tenantId);
			this.objCache.put(tenantId, tenant);
		}
		return tenant;
	}

	public Optional<ObjTenant> getByExtlKey(String extlKey) {
		Optional<ObjTenant> maybeTenant = this.tenantRepository.getByExtlKey(extlKey);
		if (maybeTenant.isPresent()) {
			ObjTenant tenant = maybeTenant.get();
			this.objCache.put(tenant.getId(), tenant);
		}
		return maybeTenant;
	}

	public EnumeratedDto getTenantEnumerated(Integer tenantId) {
		if (tenantId == null) {
			return null;
		}
		EnumeratedDto tenant = this.enumCache.getIfPresent(tenantId);
		if (tenant == null) {
			tenant = ObjTenantDtoAdapter.getInstance().asEnumerated(this.getTenant(tenantId));
			this.enumCache.put(tenantId, tenant);
		}
		return tenant;
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
