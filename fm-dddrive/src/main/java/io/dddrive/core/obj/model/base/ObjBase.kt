package io.dddrive.core.obj.model.base

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.ddd.model.base.AggregateBase
import io.dddrive.core.obj.model.Obj
import io.dddrive.core.obj.model.ObjMeta
import io.dddrive.core.obj.model.ObjPartTransition
import io.dddrive.core.obj.model.ObjRepository
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.model.ReferenceProperty
import java.time.OffsetDateTime

abstract class ObjBase(
	repository: ObjRepository<out Obj>,
) : AggregateBase(repository),
	Obj,
	ObjMeta {

	// @formatter:off
	protected val _objTypeId: BaseProperty<String> = this.addBaseProperty<String>("objTypeId", String::class.java)
	protected val _closedByUser: ReferenceProperty<ObjUser> = this.addReferenceProperty<ObjUser>("closedByUser", ObjUser::class.java)
	protected val _closedAt: BaseProperty<OffsetDateTime> = this.addBaseProperty<OffsetDateTime>("closedAt", OffsetDateTime::class.java)
	protected val _transitionList: PartListProperty<ObjPartTransition> = this.addPartListProperty<ObjPartTransition>("transitionList", ObjPartTransition::class.java)
	// @formatter:on

	override val repository: ObjRepository<*>
		get() = super.repository as ObjRepository<*>

	override val meta: ObjMeta
		get() = this

	override fun doCreate(
		aggregateId: Any,
		tenantId: Any,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		try {
			this.disableCalc()
			super.doCreate(aggregateId, tenantId, userId, timestamp)
			this._objTypeId.value = this.repository.aggregateType.id
		} finally {
			this.enableCalc()
		}
	}

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doAfterCreate(userId, timestamp)
		try {
			this.disableCalc()
			this._owner.id = userId
			this._version.value = 0
			this._createdByUser.id = userId
			this._createdAt.value = timestamp
			this._transitionList.addPart(null).init(userId, timestamp)
		} finally {
			this.enableCalc()
		}
	}

	// 	@Override
	// 	public void doAssignParts() {
	// 		super.doAssignParts();
	// 		ObjPartItemRepository itemRepository = this.getRepository().getItemRepository();
	// 		for (Property<?> property : this.getProperties()) {
	// 			if (property instanceof EnumSetProperty<?> enumSet) {
	// 				List<ObjPartItem> partList = itemRepository.getParts(this, enumSet.getPartListType());
	// 				enumSet.loadEnums(partList);
	// 			} else if (property instanceof ReferenceSetProperty<?> referenceSet) {
	// 				List<ObjPartItem> partList = itemRepository.getParts(this, referenceSet.getPartListType());
	// 				referenceSet.loadReferences(partList);
	// 			}
	// 		}
	// 	}
	override fun doBeforeStore(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		this._transitionList.addPart(null).init(userId, timestamp)

		super.doBeforeStore(userId, timestamp)

		try {
			this.disableCalc()
			this._version.value = this._version.value!! + 1
			this._modifiedByUser.id = userId
			this._modifiedAt.value = timestamp
		} finally {
			this.enableCalc()
		}
	}

	override fun delete(
		userId: Any?,
		timestamp: OffsetDateTime?,
	) {
		this._closedByUser.id = userId
		this._closedAt.value = timestamp
	}

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === this._transitionList) {
			return this.directory
				.getPartRepository(ObjPartTransition::class.java)
				.create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	protected fun setCaption(caption: String?) {
		this._caption.value = caption
	}

}
