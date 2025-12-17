package io.dddrive.core.obj.model.base;

import io.dddrive.core.ddd.model.Part;
import io.dddrive.core.ddd.model.base.AggregateBase;
import io.dddrive.core.obj.model.Obj;
import io.dddrive.core.obj.model.ObjMeta;
import io.dddrive.core.obj.model.ObjPartTransition;
import io.dddrive.core.obj.model.ObjRepository;
import io.dddrive.core.oe.model.ObjUser;
import io.dddrive.core.property.model.BaseProperty;
import io.dddrive.core.property.model.PartListProperty;
import io.dddrive.core.property.model.Property;
import io.dddrive.core.property.model.ReferenceProperty;

import java.time.OffsetDateTime;

public abstract class ObjBase extends AggregateBase implements Obj, ObjMeta {

	//@formatter:off
	protected final BaseProperty<String> objTypeId = this.addBaseProperty("objTypeId", String.class);
	protected final ReferenceProperty<ObjUser> closedByUser = this.addReferenceProperty("closedByUser", ObjUser.class);
	protected final BaseProperty<OffsetDateTime> closedAt = this.addBaseProperty("closedAt", OffsetDateTime.class);
	protected final PartListProperty<ObjPartTransition> transitionList = this.addPartListProperty("transitionList", ObjPartTransition.class);
	//@formatter:on

	protected ObjBase(ObjRepository<? extends Obj> repository) {
		super(repository);
	}

	@Override
	public ObjRepository<?> getRepository() {
		return (ObjRepository<?>) super.getRepository();
	}

	@Override
	public ObjMeta getMeta() {
		return this;
	}

	@Override
	public void doCreate(Object id, Object tenantId, Object userId, OffsetDateTime timestamp) {
		try {
			this.disableCalc();
			super.doCreate(id, tenantId, userId, timestamp);
			this.objTypeId.setValue(this.getRepository().getAggregateType().getId());
		} finally {
			this.enableCalc();
		}
	}

	@Override
	public void doAfterCreate(Object userId, OffsetDateTime timestamp) {
		super.doAfterCreate(userId, timestamp);
		try {
			this.disableCalc();
			this.get_owner().setId(userId);
			this.get_version().setValue(0);
			this.get_createdByUser().setId(userId);
			this.get_createdAt().setValue(timestamp);
			ObjPartTransitionBase transition = (ObjPartTransitionBase) this.transitionList.addPart(null);
			//transition.setSeqNr(0);
			transition.user.setId(userId);
			transition.timestamp.setValue(timestamp);
		} finally {
			this.enableCalc();
		}
	}

//	@Override
//	public void doAssignParts() {
//		super.doAssignParts();
//		ObjPartItemRepository itemRepository = this.getRepository().getItemRepository();
//		for (Property<?> property : this.getProperties()) {
//			if (property instanceof EnumSetProperty<?> enumSet) {
//				List<ObjPartItem> partList = itemRepository.getParts(this, enumSet.getPartListType());
//				enumSet.loadEnums(partList);
//			} else if (property instanceof ReferenceSetProperty<?> referenceSet) {
//				List<ObjPartItem> partList = itemRepository.getParts(this, referenceSet.getPartListType());
//				referenceSet.loadReferences(partList);
//			}
//		}
//	}

	@Override
	public void doBeforeStore(Object userId, OffsetDateTime timestamp) {

		ObjPartTransitionBase transition = (ObjPartTransitionBase) this.transitionList.addPart(null);
		//transition.setSeqNr(this.transitionList.getPartCount() - 1);
		transition.user.setId(userId);
		transition.timestamp.setValue(timestamp);

		super.doBeforeStore(userId, timestamp);

		try {
			this.disableCalc();
			this.get_version().setValue(this.get_version().getValue() + 1);
			this.get_modifiedByUser().setId(userId);
			this.get_modifiedAt().setValue(timestamp);
		} finally {
			this.enableCalc();
		}

	}

	@Override
	public void delete(Object userId, OffsetDateTime timestamp) {
		this.closedByUser.setId(userId);
		this.closedAt.setValue(timestamp);
	}

	@Override
	public Part<?> doAddPart(Property<?> property, Integer partId) {
		if (property == this.transitionList) {
			return this.getDirectory().getPartRepository(ObjPartTransition.class).create(this, property, partId);
		}
		return super.doAddPart(property, partId);
	}

	protected void setCaption(String caption) {
		this.get_caption().setValue(caption);
	}

}
