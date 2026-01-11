package dddrive.property.path

import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.Entity
import dddrive.ddd.model.Part
import dddrive.property.model.PartListProperty
import dddrive.property.model.PartMapProperty
import dddrive.property.model.Property
import dddrive.property.model.base.IdProperty

fun Entity<*>.path(): String =
	if (this is Part<*>) {
		buildPath(meta.parentProperty.path())
	} else {
		this as Aggregate
		meta.repository.aggregateType.id + "(" + id + ")"
	}

fun Entity<*>.relativePath(): String =
	if (this is Part<*>) {
		buildPath(meta.parentProperty.relativePath())
	} else {
		""
	}

private fun Part<*>.buildPath(basePath: String): String {
	val parentProperty = meta.parentProperty
	if (parentProperty is PartListProperty<*, *>) {
		var index = parentProperty.indexOf(this)
		if (index == -1) {
			index = parentProperty.size
		}
		return "$basePath[$index]"
	} else if (parentProperty is PartMapProperty<*, *>) {
		val key = parentProperty.keyOf(this as Part<*>)
		return "$basePath[\"$key\"]"
	} else {
		return "$basePath.$id"
	}
}

fun Property<*>.relativePath(): String =
	if (this is IdProperty<*, *>) {
		"${baseProperty.relativePath()}.$name"
	} else {
		val relativePath = (entity as Entity<*>).relativePath()
		if (relativePath.isEmpty()) name else "$relativePath.$name"
	}

fun Property<*>.path(): String =
	if (this is IdProperty<*, *>) {
		"${baseProperty.path()}.$name"
	} else {
		"${(entity as Entity<*>).path()}.$name"
	}
