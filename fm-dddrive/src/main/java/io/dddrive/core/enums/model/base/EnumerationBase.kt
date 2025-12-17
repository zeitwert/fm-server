package io.dddrive.core.enums.model.base

import io.dddrive.core.ddd.model.RepositoryDirectory.Companion.instance
import io.dddrive.core.ddd.model.RepositoryDirectorySPI
import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.Enumeration

abstract class EnumerationBase<E : Enumerated>(
	enumeratedClass: Class<E>,
) : Enumeration<E> {

	override lateinit var area: String
	override lateinit var module: String
	override lateinit var id: String
	val _items: MutableList<E> = mutableListOf()
	private val _itemsById: MutableMap<String, E> = mutableMapOf()

	init {
		val parts = enumeratedClass
			.getCanonicalName()
			.split("\\.".toRegex())
			.dropLastWhile { it.isEmpty() }
			.toTypedArray()
		val numOfParts = parts.size

		check(numOfParts > 5) {
			"valid enumeration class name (i), ([company/project].[area].[module].model.enums.[xyEnum]): " +
				this.javaClass.getCanonicalName()
		}
		check("model" == parts[numOfParts - 3]) {
			"valid enumeration class name (ii), must end with (model.enums.[xyEnum]): " +
				this.javaClass.getCanonicalName()
		}
		check("enums" == parts[numOfParts - 2]) {
			"valid enumeration class name (iii), must end with (model.enums.[xyEnum]): " +
				this.javaClass.getCanonicalName()
		}

		this.area = parts[numOfParts - 5]
		this.module = parts[numOfParts - 4]
		this.id = parts[numOfParts - 1][0].lowercaseChar().toString() + parts[numOfParts - 1].substring(1) + "Enum"

		(instance as RepositoryDirectorySPI).addEnumeration(enumeratedClass, this)
	}

	override val items = _items.toList()

	open fun addItem(item: E) {
		require(EnumConfigBase.isInConfig()) { "in ddd configuration" }
		require(this._itemsById[item.id] == null) { "unique item [" + item.id + "] in enumeration [" + this.id + "]" }
		EnumConfigBase.addEnum(this)
		this._items.add(item)
		this._itemsById.put(item.id, item)
	}

	open fun assignItems() {
		require(EnumConfigBase.isInConfig()) { "in ddd configuration" }
	}

	override fun getItem(id: String): E {
		val item = this._itemsById[id]
		check(item != null) { "valid item [" + id + "] in enumeration [" + this.id + "]" }
		return item
	}

	override val resourcePath: String
		get() = this.module + "." + this.id.replace("Enum", "")

}
