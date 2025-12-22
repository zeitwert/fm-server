package io.dddrive.property.model.impl

import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.PartSPI
import io.dddrive.property.model.EntityWithProperties
import io.dddrive.property.model.EntityWithPropertiesSPI
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.base.PropertyBase

open class PartListPropertyImpl<P : Part<*>>(
	entity: EntityWithProperties,
	name: String,
	override val partType: Class<P>,
) : PropertyBase<P>(entity, name),
	PartListProperty<P> {

	private val partList: MutableList<P> = mutableListOf()

	override fun clearParts() {
		require(this.isWritable) { "writable" }
		for (part in this.partList) {
			(part as PartSPI<*>).delete()
		}
		this.partList.clear()
		(this.entity as EntityWithPropertiesSPI).doAfterClear(this)
	}

	@Suppress("UNCHECKED_CAST")
	override fun addPart(partId: Int?): P {
		require(this.isWritable) { "writable" }
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
		require(0 <= seqNr && seqNr < this.partCount) { "valid seqNr (0 <= $seqNr < ${this.partCount})" }
		return this.partList.get(seqNr)
	}

	override fun getPartById(partId: Int): P = this.partList.first { it.id == partId }

	override val parts: List<P>
		get() = this.partList.toList()

	override fun removePart(partId: Int) {
		require(this.isWritable) { "writable" }
		this.removePart(this.getPartById(partId))
	}

	override fun removePart(part: P) {
		require(this.isWritable) { "writable" }
		(part as EntityWithPropertiesSPI).fireEntityRemovedChange()
		(part as PartSPI<*>).delete()
		this.partList.remove(part)
		(this.entity as EntityWithPropertiesSPI).doAfterRemove(this)
	}

	override fun getIndexOfPart(part: Part<*>): Int = this.partList.indexOf(part)

}
