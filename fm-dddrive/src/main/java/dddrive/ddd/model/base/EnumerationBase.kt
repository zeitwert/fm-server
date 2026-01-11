package dddrive.ddd.model.base

import dddrive.ddd.model.Enumerated
import dddrive.ddd.model.Enumeration
import dddrive.ddd.model.RepositoryDirectory
import dddrive.ddd.model.RepositoryDirectorySPI

abstract class EnumerationBase<E : Enumerated>(
	enumeratedClass: Class<E>,
) : Enumeration<E> {

	override lateinit var area: String
	override lateinit var module: String
	override lateinit var id: String
	override val items: MutableList<E> = mutableListOf()
	private val itemsById: MutableMap<String, E> = mutableMapOf()

	init {
		val parts = enumeratedClass
			.getCanonicalName()
			.split("\\.".toRegex())
			.dropLastWhile { it.isEmpty() }
			.toTypedArray()
		val numOfParts = parts.size

		check(parts[0] == "dddrive" || numOfParts > 5) {
			"valid enumeration class name (i), ([company/project].[area].[module].model.enums.[xyEnum]): " +
					javaClass.getCanonicalName()
		}
		check("model" == parts[numOfParts - 3]) {
			"valid enumeration class name (ii), must end with (model.enums.[xyEnum]): " +
					javaClass.getCanonicalName()
		}
		check("enums" == parts[numOfParts - 2]) {
			"valid enumeration class name (iii), must end with (model.enums.[xyEnum]): " +
					javaClass.getCanonicalName()
		}

		area = parts[numOfParts - 5]
		module = parts[numOfParts - 4]
		id = parts[numOfParts - 1][0].lowercaseChar().toString() + parts[numOfParts - 1].substring(1) + "Enum"

		(RepositoryDirectory.Companion.instance as RepositoryDirectorySPI).addEnumeration(enumeratedClass, this)
	}

	open fun addItem(item: E) {
		require(EnumConfigBase.isInConfig()) { "in ddd configuration" }
		require(itemsById[item.id] == null) { "unique item [" + item.id + "] in enumeration [" + id + "]" }
		EnumConfigBase.addEnum(this)
		items.add(item)
		itemsById[item.id] = item
	}

	open fun assignItems() {
		require(EnumConfigBase.isInConfig()) { "in ddd configuration" }
	}

	override fun getItem(id: String): E {
		val item = itemsById[id]
		check(item != null) { "valid item [" + id + "] in enumeration [" + id + "]" }
		return item
	}

	override val resourcePath: String
		get() = "$module.${id.replace("Enum", "")}"

}
