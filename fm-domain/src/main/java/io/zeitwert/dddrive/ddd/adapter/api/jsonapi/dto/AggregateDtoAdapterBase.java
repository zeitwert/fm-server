package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto;

import dddrive.app.doc.model.Doc;
import dddrive.app.obj.model.Obj;
import dddrive.ddd.core.model.Aggregate;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDto;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDtoAdapter;
import io.zeitwert.fm.oe.model.ObjTenant;
import io.zeitwert.fm.oe.model.ObjTenantRepository;
import io.zeitwert.fm.oe.model.ObjUser;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AggregateDtoAdapterBase<A extends Aggregate, D extends AggregateDto<A>>
		implements AggregateDtoAdapter<A, D> {

	private ObjTenantRepository tenantRepository = null;
	private ObjUserRepository userRepository = null;

	protected ObjTenantRepository getTenantRepository() {
		return this.tenantRepository;
	}

	@Autowired
	void setTenantRepository(ObjTenantRepository tenantRepository) {
		this.tenantRepository = tenantRepository;
	}

	protected ObjTenant getTenant(Object tenantId) {
		return this.tenantRepository.get(tenantId);
	}

	protected EnumeratedDto getTenantEnumerated(Integer tenantId) {
		return tenantId != null ? EnumeratedDto.of(tenantRepository.get(tenantId)) : null;
	}

	protected ObjUserRepository getUserRepository() {
		return this.userRepository;
	}

	@Autowired
	void setUserRepository(ObjUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	protected ObjUser getUser(Object userId) {
		return this.userRepository.get(userId);
	}

	protected EnumeratedDto getUserEnumerated(Integer userId) {
		return userId != null ? EnumeratedDto.of(userRepository.get(userId)) : null;
	}

	protected EnumeratedDto asEnumerated(Aggregate a) {
		if (a instanceof Obj) {
			return asEnumerated((Obj) a);
		} else {
			return asEnumerated((Doc) a);
		}
	}

	protected EnumeratedDto asEnumerated(Obj o) {
		return EnumeratedDto.of(o.getId().toString(), o.getCaption());
	}

	protected EnumeratedDto asEnumerated(Doc d) {
		return EnumeratedDto.of(d.getId().toString(), d.getCaption());
	}

}
