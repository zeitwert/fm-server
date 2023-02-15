
package io.zeitwert.fm.task.model.base;

import static io.dddrive.util.Invariant.assertThis;
import static io.dddrive.util.Invariant.requireThis;

import java.time.OffsetDateTime;

import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.base.AggregateRepositorySPI;
import io.dddrive.property.model.EnumProperty;
import io.dddrive.property.model.SimpleProperty;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin;
import io.zeitwert.fm.doc.model.base.FMDocBase;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.enums.CodeTaskPriority;

public abstract class DocTaskBase extends FMDocBase implements DocTask, AggregateWithNotesMixin {

	//@formatter:off
	protected final SimpleProperty<Integer> relatedObjId = this.addSimpleProperty("relatedObjId", Integer.class);
	protected final SimpleProperty<Integer> relatedDocId = this.addSimpleProperty("relatedDocId", Integer.class);
	protected final SimpleProperty<String> subject = this.addSimpleProperty("subject", String.class);
	protected final SimpleProperty<String> content = this.addSimpleProperty("content", String.class);
	protected final SimpleProperty<Boolean> isPrivate = this.addSimpleProperty("isPrivate", Boolean.class);
	protected final EnumProperty<CodeTaskPriority> priority = this.addEnumProperty("priority", CodeTaskPriority.class);
	protected final SimpleProperty<OffsetDateTime> dueAt = this.addSimpleProperty("dueAt", OffsetDateTime.class);
	protected final SimpleProperty<OffsetDateTime> remindAt = this.addSimpleProperty("remindAt", OffsetDateTime.class);
	//@formatter:on

	protected DocTaskBase(DocTaskRepository repository, Object state) {
		super(repository, state);
	}

	@Override
	public DocTaskRepository getRepository() {
		return (DocTaskRepository) super.getRepository();
	}

	@Override
	public DocTask aggregate() {
		return this;
	}

	@Override
	public void doBeforeStore() {
		super.doBeforeStore();
		requireThis(this.getRelatedToId() != null, "relatedTo not null");
		if (this.getAccountId() == null) { // TODO: set accountId to relatedTo's accountId
			assertThis(((AggregateRepositorySPI<?, ?>) this.getRepository()).getIdProvider().isObjId(this.getRelatedToId()),
					"relatedTo is obj (doc nyi)");
			this.setAccountId(((RequestContextFM) this.getMeta().getRequestContext()).getAccountId());
		}
		assertThis(this.getAccountId() != null, "account not null");
	}

	@Override
	public final ObjAccount getAccount() {
		return this.getAppContext().getBean(ObjAccountCache.class).get(this.getAccountId());
	}

	@Override
	public void doCalcSearch() {
		super.doCalcSearch();
		this.addSearchText(this.getSubject());
		this.addSearchText(this.getContent());
	}

	@Override
	public Integer getRelatedToId() {
		Integer relatedToId = this.relatedObjId.getValue();
		return relatedToId != null ? relatedToId : this.relatedObjId.getValue();
	}

	@Override
	public void setRelatedToId(Integer id) {
		if (id == null) {
			this.relatedObjId.setValue(null);
			this.relatedDocId.setValue(null);
		} else if (((AggregateRepositorySPI<?, ?>) this.getRepository()).getIdProvider().isObjId(id)) {
			this.relatedObjId.setValue(id);
			this.relatedDocId.setValue(null);
		} else {
			this.relatedObjId.setValue(null);
			this.relatedDocId.setValue(id);
		}
	}

	@Override
	public Aggregate getRelatedTo() {
		Integer relatedToId = this.relatedObjId.getValue();
		if (relatedToId != null) {
			return this.getRepository().getObjCache().get(relatedToId);
		}
		relatedToId = this.relatedDocId.getValue();
		// if (relatedId != null) {
		// return this.getRepository().getObjRepository().get(relatedId);
		// }
		return null;
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	private void calcCaption() {
		this.setCaption(this.getSubject());
	}

}
