package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto;

import org.springframework.beans.factory.annotation.Autowired;

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDto;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDtoAdapter;
import io.dddrive.ddd.model.Aggregate;
import io.dddrive.oe.model.ObjTenant;
import io.dddrive.oe.model.ObjUser;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.oe.model.ObjTenantFMRepository;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;

public abstract class AggregateDtoAdapterBase<A extends Aggregate, D extends AggregateDto<A>>
		implements AggregateDtoAdapter<A, D> {

	private ObjTenantFMRepository tenantRepository = null;
	private ObjUserFMRepository userRepository = null;

	@Autowired
	void setTenantRepository(ObjTenantFMRepository tenantRepository) {
		this.tenantRepository = tenantRepository;
	}

	@Autowired
	void setUserRepository(ObjUserFMRepository userRepository) {
		this.userRepository = userRepository;
	}

	protected ObjTenantFMRepository getTenantRepository() {
		return this.tenantRepository;
	}

	protected ObjTenant getTenant(Integer tenantId) {
		return this.tenantRepository.get(tenantId);
	}

	protected EnumeratedDto getTenantEnumerated(Integer tenantId) {
		return tenantId != null ? EnumeratedDto.of(tenantRepository.get(tenantId)) : null;
	}

	protected ObjUserFMRepository getUserRepository() {
		return this.userRepository;
	}

	protected ObjUser getUser(Integer userId) {
		return this.userRepository.get(userId);
	}

	protected EnumeratedDto getUserEnumerated(Integer userId) {
		return userId != null ? EnumeratedDto.of(userRepository.get(userId)) : null;
	}

	protected EnumeratedDto asEnumerated(Aggregate a) {
		return EnumeratedDto.of(a.getId().toString(), a.getCaption());
	}

}
