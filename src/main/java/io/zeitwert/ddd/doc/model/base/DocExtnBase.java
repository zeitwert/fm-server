
package io.zeitwert.ddd.doc.model.base;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.property.model.SimpleProperty;

import org.jooq.TableRecord;

public abstract class DocExtnBase extends DocBase {

	//@formatter:off
	protected final SimpleProperty<Integer> extnDocId = this.addSimpleProperty("extnDocId", Integer.class);
	protected final SimpleProperty<Integer> extnTenantId = this.addSimpleProperty("extnTenantId", Integer.class);
	protected final SimpleProperty<Integer> extnAccountId = this.addSimpleProperty("extnAccountId", Integer.class);
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
			this.extnTenantId.setValue(tenantId);
		} finally {
			this.enableCalc();
		}
	}

	public final Integer getAccountId() {
		return this.accountId.getValue();
	}

	public final void setAccountId(Integer id) {
		this.accountId.setValue(id);
		this.extnAccountId.setValue(id);
	}

}
