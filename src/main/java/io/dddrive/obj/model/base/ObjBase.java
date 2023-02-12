
package io.dddrive.obj.model.base;

import java.time.OffsetDateTime;
import java.util.List;

import io.dddrive.app.model.RequestContext;
import io.dddrive.ddd.model.Part;
import io.dddrive.ddd.model.base.AggregateBase;
import io.dddrive.ddd.model.enums.CodeAggregateType;
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum;
import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjMeta;
import io.dddrive.obj.model.ObjPartItem;
import io.dddrive.obj.model.ObjPartItemRepository;
import io.dddrive.obj.model.ObjPartTransition;
import io.dddrive.obj.model.ObjRepository;
import io.dddrive.oe.model.ObjTenant;
import io.dddrive.oe.model.ObjUser;
import io.dddrive.property.model.EnumSetProperty;
import io.dddrive.property.model.PartListProperty;
import io.dddrive.property.model.Property;
import io.dddrive.property.model.ReferenceProperty;
import io.dddrive.property.model.ReferenceSetProperty;
import io.dddrive.property.model.SimpleProperty;

public abstract class ObjBase extends AggregateBase implements Obj, ObjMeta {

	//@formatter:off
	protected final SimpleProperty<Integer> id = this.addSimpleProperty("id", Integer.class);
	protected final SimpleProperty<String> objTypeId = this.addSimpleProperty("objTypeId", String.class);
	protected final ReferenceProperty<ObjTenant> tenant = this.addReferenceProperty("tenant", ObjTenant.class);
	protected final SimpleProperty<Integer> accountId = this.addSimpleProperty("accountId", Integer.class);
	protected final ReferenceProperty<ObjUser> owner = this.addReferenceProperty("owner", ObjUser.class);
	protected final SimpleProperty<String> caption = this.addSimpleProperty("caption", String.class);
	protected final SimpleProperty<Integer> version = this.addSimpleProperty("version", Integer.class);
	protected final ReferenceProperty<ObjUser> createdByUser = this.addReferenceProperty("createdByUser", ObjUser.class);
	protected final SimpleProperty<OffsetDateTime> createdAt = this.addSimpleProperty("createdAt", OffsetDateTime.class);
	protected final ReferenceProperty<ObjUser> modifiedByUser = this.addReferenceProperty("modifiedByUser", ObjUser.class);
	protected final SimpleProperty<OffsetDateTime> modifiedAt = this.addSimpleProperty("modifiedAt", OffsetDateTime.class);
	protected final ReferenceProperty<ObjUser> closedByUser = this.addReferenceProperty("closedByUser", ObjUser.class);
	protected final SimpleProperty<OffsetDateTime> closedAt = this.addSimpleProperty("closedAt", OffsetDateTime.class);
	protected final PartListProperty<ObjPartTransition> transitionList = this.addPartListProperty("transitionList", ObjPartTransition.class);
	//@formatter:on

	protected ObjBase(ObjRepository<? extends Obj, ? extends Object> repository, Object state) {
		super(repository, state);
	}

	@Override
	public ObjRepository<?, ?> getRepository() {
		return (ObjRepository<?, ?>) super.getRepository();
	}

	@Override
	public ObjMeta getMeta() {
		return this;
	}

	@Override
	public RequestContext getRequestContext() {
		return this.getAppContext().getRequestContext();
	}

	@Override
	public Integer getTenantId() {
		return this.tenant.getId();
	}

	@Override
	public CodeAggregateType getAggregateType() {
		return CodeAggregateTypeEnum.getAggregateType(this.objTypeId.getValue());
	}

	@Override
	public void doInit(Integer id, Integer tenantId) {
		super.doInit(id, tenantId);
		try {
			this.disableCalc();
			this.objTypeId.setValue(this.getRepository().getAggregateType().getId());
			this.id.setValue(id);
			this.tenant.setId(tenantId);
		} finally {
			this.enableCalc();
		}
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		Integer sessionUserId = this.getMeta().getRequestContext().getUser().getId();
		try {
			this.disableCalc();
			this.owner.setId(sessionUserId);
			this.version.setValue(0);
			this.createdByUser.setId(sessionUserId);
			OffsetDateTime now = this.getMeta().getRequestContext().getCurrentTime();
			this.createdAt.setValue(now);
			ObjPartTransitionBase transition = (ObjPartTransitionBase) this.transitionList.addPart();
			transition.setSeqNr(0);
			transition.timestamp.setValue(now);
		} finally {
			this.enableCalc();
		}
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjPartItemRepository itemRepository = this.getRepository().getItemRepository();
		for (Property<?> property : this.getProperties()) {
			if (property instanceof EnumSetProperty<?>) {
				EnumSetProperty<?> enumSet = (EnumSetProperty<?>) property;
				List<ObjPartItem> partList = itemRepository.getParts(this, enumSet.getPartListType());
				enumSet.loadEnums(partList);
			} else if (property instanceof ReferenceSetProperty<?>) {
				ReferenceSetProperty<?> referenceSet = (ReferenceSetProperty<?>) property;
				List<ObjPartItem> partList = itemRepository.getParts(this, referenceSet.getPartListType());
				referenceSet.loadReferences(partList);
			}
		}
	}

	@Override
	public void doBeforeStore() {

		RequestContext requestCtx = this.getMeta().getRequestContext();
		OffsetDateTime now = requestCtx.getCurrentTime();
		ObjPartTransitionBase transition = (ObjPartTransitionBase) this.transitionList.addPart();
		transition.setSeqNr(this.transitionList.getPartCount() - 1);
		transition.timestamp.setValue(now);

		super.doBeforeStore();

		try {
			this.disableCalc();
			this.version.setValue(this.version.getValue() + 1);
			this.modifiedByUser.setValue(requestCtx.getUser());
			this.modifiedAt.setValue(requestCtx.getCurrentTime());
		} finally {
			this.enableCalc();
		}

	}

	@Override
	public void delete() {
		RequestContext requestCtx = this.getMeta().getRequestContext();
		this.closedByUser.setValue(requestCtx.getUser());
		this.closedAt.setValue(requestCtx.getCurrentTime());
	}

	@Override
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		if (property instanceof EnumSetProperty<?>) {
			return this.getRepository().getItemRepository().create(this, partListType);
		} else if (property instanceof ReferenceSetProperty<?>) {
			return this.getRepository().getItemRepository().create(this, partListType);
		}
		return super.addPart(property, partListType);
	}

	protected void setCaption(String caption) {
		this.caption.setValue(caption);
	}

}
