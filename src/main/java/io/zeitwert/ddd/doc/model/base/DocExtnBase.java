
package io.zeitwert.ddd.doc.model.base;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;

import org.jooq.TableRecord;

public abstract class DocExtnBase extends DocBase {

	//@formatter:off
	protected final SimpleProperty<Integer> extnDocId = this.addSimpleProperty("extnDocId", Integer.class);
	protected final ReferenceProperty<ObjTenant> extnTenantId = this.addReferenceProperty("extnTenantId", ObjTenant.class);
	//@formatter:on

	protected DocExtnBase(DocRepository<? extends Doc, ? extends TableRecord<?>> repository, Object state) {
		super(repository, state);
	}

	@Override
	public final void doInit(Integer id, Integer tenantId) {
		super.doInit(id, tenantId);
		try {
			this.disableCalc();
			this.extnDocId.setValue(id);
			this.extnTenantId.setId(tenantId);
		} finally {
			this.enableCalc();
		}
	}

}
