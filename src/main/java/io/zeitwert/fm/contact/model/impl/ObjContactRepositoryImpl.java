
package io.zeitwert.fm.contact.model.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddressRepository;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.contact.model.base.ObjContactBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactVRecord;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;

@Component("objContactRepository")
public class ObjContactRepositoryImpl extends FMObjRepositoryBase<ObjContact, ObjContactVRecord>
		implements ObjContactRepository {

	private static final String AGGREGATE_TYPE = "obj_contact";

	private ObjContactPartAddressRepository addressRepository;

	protected ObjContactRepositoryImpl(AppContext appContext) {
		super(ObjContactRepository.class, ObjContact.class, ObjContactBase.class, AGGREGATE_TYPE, appContext);
	}

	@Override
	public ObjContactPartAddressRepository getAddressRepository() {
		if (this.addressRepository == null) {
			this.addressRepository = AppContext.getInstance().getBean(ObjContactPartAddressRepository.class);
		}
		return this.addressRepository;
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getAddressRepository());
	}

	@Override
	protected boolean hasAccountId() {
		return true;
	}

	@Override
	public List<ObjContactVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_CONTACT_V, Tables.OBJ_CONTACT_V.ID, querySpec);
	}

}
