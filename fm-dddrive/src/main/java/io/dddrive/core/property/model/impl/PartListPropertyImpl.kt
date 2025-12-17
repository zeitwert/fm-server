package io.dddrive.core.property.model.impl

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.ddd.model.PartSPI
import io.dddrive.core.property.model.EntityWithProperties
import io.dddrive.core.property.model.EntityWithPropertiesSPI
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.core.property.model.base.PropertyBase
import io.dddrive.util.Invariant

open class PartListPropertyImpl<P : Part<*>>(
	entity: EntityWithProperties,
	name: String,
	override val partType: Class<P>,
) : PropertyBase<P>(entity, name),
	PartListProperty<P> {

	private val partList: MutableList<P> = mutableListOf()

	override fun clearParts() {
		Invariant.requireThis(this.isWritable, "not frozen")
		for (part in this.partList) {
			(part as PartSPI<*>).delete()
		}
		this.partList.clear()
		(this.entity as EntityWithPropertiesSPI).doAfterClear(this)
	}

	@Suppress("UNCHECKED_CAST")
	override fun addPart(partId: Int?): P {
		Invariant.requireThis(this.isWritable, "not frozen")
		val entity = this.entity as EntityWithPropertiesSPI
		val part = entity.doAddPart(this, partId) as P
		this.partList.add(part)
		(part as EntityWithPropertiesSPI).fireEntityAddedChange(part.id)
		entity.doAfterAdd(this, part)
		return part
	}

	override val partCount: Int
		get() = this.partList.size

	override fun getPart(seqNr: Int): P {
		Invariant.assertThis(0 <= seqNr && seqNr < this.partCount, "valid seqNr ($seqNr)")
		return this.partList.get(seqNr)
	}

	override fun getPartById(partId: Int): P = this.partList.first { it.id == partId }

	override val parts: List<P>
		get() = this.partList.toList()

	override fun removePart(partId: Int) {
		Invariant.requireThis(this.isWritable, "not frozen")
		this.removePart(this.getPartById(partId))
	}

	override fun removePart(part: P) {
		Invariant.requireThis(this.isWritable, "not frozen")
		(part as EntityWithPropertiesSPI).fireEntityRemovedChange()
		(part as PartSPI<*>).delete()
		this.partList.remove(part)
		(this.entity as EntityWithPropertiesSPI).doAfterRemove(this)
	}

	override fun getIndexOfPart(part: Part<*>): Int = this.partList.indexOf(part)

}
