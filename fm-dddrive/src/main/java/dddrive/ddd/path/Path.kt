package dddrive.ddd.path

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Entity
import dddrive.ddd.core.model.Part
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.Property
import dddrive.ddd.property.model.base.IdProperty

fun Entity<*>.path(): String =
	if (this is Part<*>) {
		buildPath(meta.parentProperty.path())
	} else {
		(this as Aggregate)
			.meta.repository.aggregateType.id + "(" + id + ")"
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
