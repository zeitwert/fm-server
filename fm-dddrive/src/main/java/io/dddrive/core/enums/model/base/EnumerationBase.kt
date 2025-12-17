package io.dddrive.core.enums.model.base

import io.dddrive.core.ddd.model.RepositoryDirectory.Companion.instance
import io.dddrive.core.ddd.model.RepositoryDirectorySPI
import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.Enumeration
import io.dddrive.util.Invariant
import io.dddrive.util.Invariant.MessageProvider

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

		Invariant.assertThis(
			numOfParts > 5,
			MessageProvider {
				(
					"valid enumeration class name (i), ([company/project].[area].[module].model.enums.[xyEnum]): " +
						this.javaClass.getCanonicalName()
				)
			},
		)
		Invariant.assertThis(
			"model" == parts[numOfParts - 3],
			MessageProvider {
				(
					"valid enumeration class name (ii), must end with (model.enums.[xyEnum]): " +
						this.javaClass.getCanonicalName()
				)
			},
		)
		Invariant.assertThis(
			"enums" == parts[numOfParts - 2],
			MessageProvider {
				(
					"valid enumeration class name (iii), must end with (model.enums.[xyEnum]): " +
						this.javaClass.getCanonicalName()
				)
			},
		)

		this.area = parts[numOfParts - 5]
		this.module = parts[numOfParts - 4]
		this.id = parts[numOfParts - 1][0].lowercaseChar().toString() + parts[numOfParts - 1].substring(1) + "Enum"

		(instance as RepositoryDirectorySPI).addEnumeration(enumeratedClass, this)
	}

	override val items = _items.toList()

	open fun addItem(item: E) {
		Invariant.requireThis(EnumConfigBase.isInConfig(), MessageProvider { "in ddd configuration" })
		Invariant.requireThis(
			this._itemsById[item.id] == null,
			MessageProvider { "unique item [" + item.id + "] in enumeration [" + this.id + "]" },
		)
		EnumConfigBase.addEnum(this)
		this._items.add(item)
		this._itemsById.put(item.id, item)
	}

	open fun assignItems() {
		Invariant.requireThis(EnumConfigBase.isInConfig(), MessageProvider { "in ddd configuration" })
	}

	override fun getItem(id: String): E {
		val item = this._itemsById[id]
		Invariant.assertThis(item != null, MessageProvider { "valid item [" + id + "] in enumeration [" + this.id + "]" })
		return item as E
	}

	override val resourcePath: String
		get() = this.module + "." + this.id.replace("Enum", "")

}
