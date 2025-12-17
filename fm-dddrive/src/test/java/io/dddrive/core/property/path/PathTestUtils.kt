package io.dddrive.core.property.path

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.AggregateMeta
import io.dddrive.core.ddd.model.Part
import io.dddrive.core.ddd.model.PartMeta
import io.dddrive.core.ddd.model.RepositoryDirectory
import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.Enumeration
import io.dddrive.core.oe.model.ObjTenant
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EntityWithProperties
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.core.property.model.PartReferenceProperty
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.model.ReferenceProperty
import io.dddrive.core.property.model.base.EntityWithPropertiesBase

/**
 * Test utilities for path handling framework tests
 */
object PathTestUtils {

	/**
	 * Creates a mock EntityWithProperties for testing
	 */
	fun createMockEntity(): TestEntity = TestEntity()

	/**
	 * Creates a mock enumeration for testing
	 */
	fun createMockEnumeration(): Enumeration<TestEnum> =
		object : Enumeration<TestEnum> {
			private val items =
				listOf(
					TestEnum("active", "Active"),
					TestEnum("inactive", "Inactive"),
					TestEnum("pending", "Pending"),
					TestEnum("completed", "Completed"),
					TestEnum("archived", "Archived"),
					TestEnum("value1", "Value 1"),
					TestEnum("value2", "Value 2"),
				)

			override fun getArea(): String = "test"

			override fun getModule(): String = "test"

			override fun getId(): String = "TestEnumeration"

			override fun getResourcePath(): String = "test/enum"

			override fun getItem(id: String): TestEnum? = items.find { it.getId() == id }

			override fun getItems(): List<TestEnum> = items
		}

	/**
	 * Creates a BaseProperty with specified type and value for testing
	 */
	fun <T> createBaseProperty(
		type: Class<T>,
		initialValue: T? = null,
	): BaseProperty<T> =
		object : BaseProperty<T> {

			override var value: T? = initialValue

			override val type: Class<T> = type

			override val entity: EntityWithProperties = TestEntity()

			override val relativePath: String = "test"

			override val path: String = "test"

			override val name: String = "test"

			override val isWritable: Boolean = true
		}

	/**
	 * Creates an EnumProperty for testing
	 */
	fun createEnumProperty(
		enumeration: Enumeration<TestEnum>,
		initialValue: TestEnum? = null,
	): EnumProperty<TestEnum> =
		object : EnumProperty<TestEnum> {
			override var value: TestEnum? = initialValue

			override var id: String? = initialValue?.getId()

			override val idProperty: BaseProperty<String> = createBaseProperty(String::class.java, initialValue?.getId())

			override val enumeration: Enumeration<TestEnum> = enumeration

			override val type: Class<TestEnum> = TestEnum::class.java

			override val entity: EntityWithProperties = TestEntity()

			override val relativePath: String = "test"

			override val path: String = "test"

			override val name: String = "test"

			override val isWritable: Boolean = true
		}

	/**
	 * Creates a PartListProperty for testing
	 */
	fun createPartListProperty(): PartListProperty<TestPart> {
		return object : PartListProperty<TestPart> {
			private val _parts = mutableListOf<TestPart>()
			private var nextId = 1

			override val partType: Class<TestPart> = TestPart::class.java

			override val partCount: Int get() = _parts.size

			override fun getPart(seqNr: Int): TestPart = _parts[seqNr]

			override fun getPartById(partId: Int): TestPart = _parts.find { it.id == partId }!!

			override val parts: List<TestPart> get() = _parts.toList()

			override fun clearParts() {
				_parts.clear()
			}

			override fun addPart(partId: Int?): TestPart {
				val id = partId ?: nextId++
				val newPart = TestPart(id)

				// Add common properties that tests expect
				newPart.addProperty("name", createBaseProperty(String::class.java, "Part$id"))

				// Add enum property for status
				val enumeration = createMockEnumeration()
				newPart.addProperty("status", createEnumProperty(enumeration, enumeration.getItem("active")))

				// Add reference property for complex testing - create aggregate with children list
				val referenceAggregate = TestAggregate(100 + id)
				referenceAggregate.addProperty("name", createBaseProperty(String::class.java, "RefAggregate$id"))
				referenceAggregate.addProperty("children", createPartListProperty())
				newPart.addProperty("reference", createReferenceProperty(referenceAggregate))

				// Add pillarTwoStatement reference property for ReferencePropertyIdHandler testing
				val pillarTwoStatement = TestAggregate(200 + id)
				pillarTwoStatement.addProperty("name", createBaseProperty(String::class.java, "PillarTwoStatement$id"))
				newPart.addProperty("pillarTwoStatement", createReferenceProperty(pillarTwoStatement))

				// Add gender enum property for testing enum vs reference conflicts
				val genderEnumeration = createMockEnumeration()
				newPart.addProperty("gender", createEnumProperty(genderEnumeration, genderEnumeration.getItem("active")))

				_parts.add(newPart)
				return newPart
			}

			override fun removePart(partId: Int) {
				_parts.removeIf { it.id == partId }
			}

			override fun removePart(part: TestPart) {
				_parts.remove(part)
			}

			override fun getIndexOfPart(part: Part<*>): Int = _parts.indexOfFirst { it.id == part.id }

			override val entity: EntityWithProperties = TestEntity()

			override val relativePath: String = "test"

			override val path: String = "test"

			override val name: String = "test"

			override val isWritable: Boolean = true
		}
	}

	/**
	 * Creates a ReferenceProperty for testing
	 */
	fun createReferenceProperty(initialValue: TestAggregate? = null): ReferenceProperty<TestAggregate> =
		object : ReferenceProperty<TestAggregate> {
			override var value: TestAggregate? = initialValue

			override var id: Any? = initialValue?.id

			override val idProperty: BaseProperty<Any> = createBaseProperty(Any::class.java, initialValue?.id)

			override val type: Class<TestAggregate> = TestAggregate::class.java

			override val entity: EntityWithProperties = TestEntity()

			override val relativePath: String = "test"

			override val path: String = "test"

			override val name: String = "test"

			override val isWritable: Boolean = true
		}

	/**
	 * Creates a PartReferenceProperty for testing
	 */
	fun createPartReferenceProperty(initialValue: TestPart? = null): PartReferenceProperty<TestPart> =
		object : PartReferenceProperty<TestPart> {
			override var value: TestPart? = initialValue

			override var id: Int? = initialValue?.id

			override val idProperty: BaseProperty<Int> = createBaseProperty(Int::class.java, initialValue?.id)

			override val type: Class<TestPart> = TestPart::class.java

			override val entity: EntityWithProperties = TestEntity()

			override val relativePath: String = "test"

			override val path: String = "test"

			override val name: String = "test"

			override val isWritable: Boolean = true
		}
}

/**
 * Test enumerated value
 */
data class TestEnum(
	private val id: String,
	private val name: String,
) : Enumerated {

	override fun getId(): String = id

	override fun getName(): String = name

	override fun getEnumeration(): Enumeration<out Enumerated> = PathTestUtils.createMockEnumeration()
}

/**
 * Test entity implementing EntityWithProperties
 */
class TestEntity : EntityWithPropertiesBase() {

	private val _properties = mutableMapOf<String, Property<*>>()

	fun addProperty(
		name: String,
		property: Property<*>,
	) {
		_properties[name] = property
	}

	override val directory: RepositoryDirectory get() = null!!

	override val isInLoad: Boolean = false

	override fun isInCalc(): Boolean = false

	override val relativePath: String = "relPath"

	override val path: String = "path"

	override val isFrozen: Boolean = false

	override fun hasProperty(name: String): Boolean = _properties.containsKey(name)

	override fun getProperty(name: String): Property<*> = _properties[name] ?: throw IllegalArgumentException("Property '$name' not found")

	override val properties: List<Property<*>> get() = _properties.values.toList()

	override fun hasPart(partId: Int): Boolean = false

	override fun getPart(partId: Int): Part<*> = throw IllegalArgumentException("Part '$partId' not found")

	override fun <T> setValueByPath(
		relativePath: String,
		value: T?,
	) {
	}

	override fun <T> getPropertyByPath(path: String): Property<T> {
		val segments = path.split('.')
		var currentEntity: Any = this

		for (i in 0 until segments.size - 1) {
			val segment = segments[i]
			val property = (currentEntity as EntityWithProperties).getProperty(segment)

			currentEntity =
				when (property) {
					is ReferenceProperty<*> -> property.value ?: throw IllegalArgumentException("Reference is null")
					is PartReferenceProperty<*> -> property.value ?: throw IllegalArgumentException("Part reference is null")
					else -> throw IllegalArgumentException("Cannot navigate through property of type ${property.javaClass.simpleName}")
				}
		}

		val finalProperty = (currentEntity as EntityWithProperties).getProperty(segments.last())
		@Suppress("UNCHECKED_CAST")
		return finalProperty as Property<T>
	}

	override fun doLogChange(propertyName: String): Boolean = false

}

/**
 * Test aggregate for testing
 */
class TestAggregate(
	override val id: Int = 1,
) : Aggregate {

	private val _properties = mutableMapOf<String, Property<*>>()

	fun addProperty(
		name: String,
		property: Property<*>,
	) {
		_properties[name] = property
	}

	override val tenantId: Any get() = null!!

	override val tenant: ObjTenant get() = null!!

	override var owner: ObjUser
		get() = null!!
		set(value) {}

	override val caption: String = "TestAggregate"

	override val meta: AggregateMeta get() = null!!

	override fun <T> setValueByPath(
		relativePath: String,
		value: T?,
	) {
	}

	override val isFrozen: Boolean = false

	override fun hasProperty(name: String): Boolean = _properties.containsKey(name)

	override fun getProperty(name: String): Property<*> = _properties[name] ?: throw IllegalArgumentException("Property '$name' not found")

	override val properties: List<Property<*>> get() = _properties.values.toList()

	override fun hasPart(partId: Int): Boolean = false

	override fun getPart(partId: Int): Part<*> = throw IllegalArgumentException("Part '$partId' not found")

	fun <T> getPropertyByPath(path: String): Property<T> {
		@Suppress("UNCHECKED_CAST")
		return getProperty(path) as Property<T>
	}
}

/**
 * Test part class implementing Part
 */
class TestPart(
	override val id: Int = 1,
) : Part<TestAggregate> {

	private val _properties = mutableMapOf<String, Property<*>>()
	override val aggregate = TestAggregate(1)

	fun addProperty(
		name: String,
		property: Property<*>,
	) {
		_properties[name] = property
	}

	override val meta: PartMeta<TestAggregate> get() = null!!

	override val isFrozen: Boolean = false

	override fun hasProperty(name: String): Boolean = _properties.containsKey(name)

	override fun getProperty(name: String): Property<*> = _properties[name] ?: throw IllegalArgumentException("Property '$name' not found")

	override val properties: List<Property<*>> get() = _properties.values.toList()

	override fun hasPart(partId: Int): Boolean = false

	override fun getPart(partId: Int): Part<*> = throw IllegalArgumentException("Part '$partId' not found")

	override fun <T> setValueByPath(
		relativePath: String,
		value: T?,
	) {
	}

	fun <T> getPropertyByPath(path: String): Property<T> {
		// Simple implementation for testing
		@Suppress("UNCHECKED_CAST")
		return getProperty(path) as Property<T>
	}

}
