package io.dddrive.property.model

import io.dddrive.ddd.model.Part

interface PartListProperty<P : Part<*>> : Property<P> {

	val partType: Class<P>

	val partCount: Int

	fun getPart(seqNr: Int): P

	fun getPartById(partId: Int): P

	val parts: List<P>

	fun clearParts()

	fun addPart(partId: Int?): P

	fun removePart(partId: Int)

	fun removePart(part: P)

	/**
	 * Returns the index of the specified part within this list.
	 *
	 * @param part the part to find
	 * @return the index of the part, or -1 if the part is not in this list.
	 */
	fun getIndexOfPart(part: Part<*>): Int

}
