package io.dddrive.obj.model.base

import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.base.AggregateBase
import io.dddrive.obj.model.Obj
import io.dddrive.obj.model.ObjMeta
import io.dddrive.obj.model.ObjPartTransition
import io.dddrive.obj.model.ObjRepository
import io.dddrive.oe.model.ObjUser
import io.dddrive.path.setValueByPath
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.Property
import java.time.OffsetDateTime

abstract class ObjBase(
	override val repository: ObjRepository<out Obj>,
	isNew: Boolean,
) : AggregateBase(repository, isNew),
	Obj,
	ObjMeta {

	private lateinit var _transitionList: PartListProperty<ObjPartTransition>

	override val meta: ObjMeta
		get() = this

	override val objTypeId get() = repository.aggregateType.id

	override fun doInit() {
		super.doInit()
		addReferenceProperty("closedByUser", ObjUser::class.java)
		addBaseProperty("closedAt", OffsetDateTime::class.java)
		_transitionList = addPartListProperty("transitionList", ObjPartTransition::class.java)
	}

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doAfterCreate(userId, timestamp)
		try {
			disableCalc()
			_transitionList.addPart(null).init(userId, timestamp)
		} finally {
			enableCalc()
		}
	}

	// 	@Override
	// 	public void doAssignParts() {
	// 		super.doAssignParts();
	// 		ObjPartItemRepository itemRepository = getRepository().getItemRepository();
	// 		for (Property<?> property : getProperties()) {
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
		_transitionList.addPart(null).init(userId, timestamp)

		super.doBeforeStore(userId, timestamp)

		try {
			disableCalc()
			setValueByPath("version", meta.version + 1)
			setValueByPath("modifiedByUserId", userId)
			setValueByPath("modifiedAt", timestamp)
		} finally {
			enableCalc()
		}
	}

	override fun delete(
		userId: Any?,
		timestamp: OffsetDateTime?,
	) {
		setValueByPath("closedByUserId", userId)
		setValueByPath("closedAt", timestamp)
	}

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === _transitionList) {
			return directory
				.getPartRepository(ObjPartTransition::class.java)
				.create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	protected fun setCaption(caption: String?) {
		setValueByPath("caption", caption)
	}

}
