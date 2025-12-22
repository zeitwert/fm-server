package io.dddrive.path

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.AggregateMeta
import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.PartMeta
import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.Enumeration
import io.dddrive.oe.model.ObjTenant
import io.dddrive.oe.model.ObjUser
import io.dddrive.path.getPropertyByPath
import io.dddrive.path.getValueByPath
import io.dddrive.path.setValueByPath
import io.dddrive.property.model.AggregateReferenceProperty
import io.dddrive.property.model.BaseProperty
import io.dddrive.property.model.EntityWithProperties
import io.dddrive.property.model.EnumProperty
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.PartReferenceProperty
import io.dddrive.property.model.Property
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

/**
 * Tests for PathAccess extension functions.
 */
class PathAccessTest {

	private lateinit var rootEntity: TestEntity

	@BeforeEach
	fun setUp() {
		rootEntity = createComplexTestEntity()
	}

	@Nested
	inner class SimplePropertyAccess {

		@Test
		fun `getValueByPath returns simple property value`() {
			val result: String? = rootEntity.getValueByPath("name")
			assertEquals("TestEntity", result)
		}

		@Test
		fun `setValueByPath sets simple property value`() {
			rootEntity.setValueByPath("name", "UpdatedName")
			assertEquals("UpdatedName", rootEntity.getValueByPath("name"))
		}

		@Test
		fun `getPropertyByPath returns property object`() {
			val property: Property<String>? = rootEntity.getPropertyByPath("name")
			assertNotNull(property)
			// Verify we can get the value from the resolved property
			assertEquals("TestEntity", (property as? BaseProperty<*>)?.value)
		}
	}

	@Nested
	inner class EnumPropertyAccess {

		@Test
		fun `getValueByPath returns enum instance`() {
			val result: TestEnum? = rootEntity.getValueByPath("status")
			assertNotNull(result)
			assertEquals("active", (result as TestEnum).id)
		}

		@Test
		fun `getValueByPath with dot id returns enum ID string`() {
			val result: String? = rootEntity.getValueByPath("status.id")
			assertEquals("active", result)
		}

		@Test
		fun `getValueByPath with Id suffix returns enum ID string`() {
			val result: String? = rootEntity.getValueByPath("statusId")
			assertEquals("active", result)
		}

		@Test
		fun `setValueByPath with Id suffix sets enum by ID`() {
			rootEntity.setValueByPath("statusId", "inactive")
			assertEquals("inactive", rootEntity.getValueByPath("statusId"))
		}

		@Test
		fun `setValueByPath with dot id crashes`() {
			assertThrows(IllegalStateException::class.java) {
				rootEntity.setValueByPath("status.id", "inactive")
			}
		}
	}

	@Nested
	inner class ListPropertyAccess {

		@Test
		fun `getValueByPath with bracket syntax works`() {
			rootEntity.setValueByPath("children[0].name", "Child1")
			val result: String? = rootEntity.getValueByPath("children[0].name")
			assertEquals("Child1", result)
		}

		@Test
		fun `getValueByPath with dot syntax works`() {
			rootEntity.setValueByPath("children.0.name", "Child1")
			val result: String? = rootEntity.getValueByPath("children.0.name")
			assertEquals("Child1", result)
		}

		@Test
		fun `setValueByPath auto-expands list`() {
			rootEntity.setValueByPath("children[2].name", "Child3")
			val result: String? = rootEntity.getValueByPath("children[2].name")
			assertEquals("Child3", result)
		}

		@Test
		fun `getValueByPath returns null for missing index`() {
			val result: String? = rootEntity.getValueByPath("children[5].name")
			assertNull(result)
		}

		@Test
		fun `list access with enum id suffix works`() {
			rootEntity.setValueByPath("children[0].statusId", "pending")
			assertEquals("pending", rootEntity.getValueByPath("children[0].statusId"))
		}

		@Test
		fun `list access with enum dot id works for getter`() {
			rootEntity.setValueByPath("children[0].statusId", "pending")
			assertEquals("pending", rootEntity.getValueByPath("children[0].status.id"))
		}
	}

	@Nested
	inner class ReferencePropertyAccess {

		@Test
		fun `getValueByPath navigates through reference`() {
			val result: String? = rootEntity.getValueByPath("reference.name")
			assertEquals("ReferencedEntity", result)
		}

		@Test
		fun `setValueByPath navigates through reference`() {
			rootEntity.setValueByPath("reference.name", "UpdatedReference")
			assertEquals("UpdatedReference", rootEntity.getValueByPath("reference.name"))
		}

		@Test
		fun `getValueByPath with Id suffix returns reference ID`() {
			val result: Any? = rootEntity.getValueByPath("referenceId")
			assertNotNull(result)
		}

		@Test
		fun `setValueByPath with Id suffix sets reference ID`() {
			rootEntity.setValueByPath("referenceId", "new-id-123")
			assertEquals("new-id-123", rootEntity.getValueByPath("referenceId"))
		}

		@Test
		fun `getValueByPath with dot id returns reference ID`() {
			val result: Any? = rootEntity.getValueByPath("reference.id")
			assertNotNull(result)
		}

		@Test
		fun `setValueByPath with dot id crashes`() {
			assertThrows(IllegalStateException::class.java) {
				rootEntity.setValueByPath("reference.id", "new-id")
			}
		}
	}

	@Nested
	inner class PartReferencePropertyAccess {

		@Test
		fun `getValueByPath navigates through part reference`() {
			val result: String? = rootEntity.getValueByPath("part.name")
			assertEquals("PartEntity", result)
		}

		@Test
		fun `setValueByPath navigates through part reference`() {
			rootEntity.setValueByPath("part.name", "UpdatedPart")
			assertEquals("UpdatedPart", rootEntity.getValueByPath("part.name"))
		}
	}

	@Nested
	inner class NullHandling {

		@Test
		fun `getValueByPath returns null for null reference`() {
			rootEntity.setReferenceToNull()
			val result: String? = rootEntity.getValueByPath("reference.name")
			assertNull(result)
		}

		@Test
		fun `setValueByPath crashes for null reference`() {
			rootEntity.setReferenceToNull()
			assertThrows(IllegalStateException::class.java) {
				rootEntity.setValueByPath("reference.name", "value")
			}
		}

		@Test
		fun `getValueByPath returns null for deep null path`() {
			rootEntity.setReferenceToNull()
			val result: String? = rootEntity.getValueByPath("reference.children[0].name")
			assertNull(result)
		}
	}

	@Nested
	inner class ComplexPaths {

		@Test
		fun `complex nested path through reference and list`() {
			rootEntity.setValueByPath("reference.children[0].statusId", "completed")
			val result: String? = rootEntity.getValueByPath("reference.children[0].statusId")
			assertEquals("completed", result)
		}

		@Test
		fun `multiple list navigations work`() {
			rootEntity.setValueByPath("children[0].name", "FirstChild")
			rootEntity.setValueByPath("children[1].name", "SecondChild")

			assertEquals("FirstChild", rootEntity.getValueByPath("children[0].name"))
			assertEquals("SecondChild", rootEntity.getValueByPath("children[1].name"))
		}
	}

	@Nested
	inner class LiteralIdProperty {

		@Test
		fun `literal somethingId property takes precedence over Id suffix`() {
			// Add a literal property named "literalId"
			rootEntity.addProperty("literalId", createBaseProperty(String::class.java, "literal-value"))

			val result: String? = rootEntity.getValueByPath("literalId")
			assertEquals("literal-value", result)
		}
	}

	@Nested
	inner class ErrorCases {

		@Test
		fun `getValueByPath throws for invalid property`() {
			assertThrows(NullPointerException::class.java) {
				rootEntity.getValueByPath<Any>("nonexistent")
			}
		}

		@Test
		fun `setValueByPath throws for invalid property`() {
			assertThrows(NullPointerException::class.java) {
				rootEntity.setValueByPath("nonexistent", "value")
			}
		}

		@Test
		fun `navigation through non-reference property throws`() {
			assertThrows(IllegalStateException::class.java) {
				rootEntity.getValueByPath<Any>("name.something")
			}
		}
	}

	// Test helper methods

	private fun createComplexTestEntity(): TestEntity {
		val entity = TestEntity()

		// Add basic properties
		entity.addProperty("name", createBaseProperty(String::class.java, "TestEntity"))
		entity.addProperty("age", createBaseProperty(Int::class.java, 25))

		// Add enum property
		val enumeration = createMockEnumeration()
		entity.addProperty("status", createEnumProperty(enumeration, enumeration.getItem("active")))

		// Add list property
		entity.addProperty("children", createPartListProperty())

		// Add reference property
		val referencedEntity = createReferencedEntity()
		entity.addProperty("reference", createReferenceProperty(referencedEntity))

		// Add part reference property
		val partEntity = createPartEntity()
		entity.addProperty("part", createPartReferenceProperty(partEntity))

		return entity
	}

	private fun createReferencedEntity(): TestAggregate {
		val entity = TestAggregate()
		entity.addProperty("name", createBaseProperty(String::class.java, "ReferencedEntity"))
		entity.addProperty("children", createPartListProperty())
		return entity
	}

	private fun createPartEntity(): TestPart {
		val part = TestPart()
		part.addProperty("name", createBaseProperty(String::class.java, "PartEntity"))

		val enumeration = createMockEnumeration()
		part.addProperty("status", createEnumProperty(enumeration, enumeration.getItem("active")))

		return part
	}

	private fun createMockEnumeration(): Enumeration<TestEnum> =
		object : Enumeration<TestEnum> {
			override val items =
				listOf(
					TestEnum("active", "Active"),
					TestEnum("inactive", "Inactive"),
					TestEnum("pending", "Pending"),
					TestEnum("completed", "Completed"),
				)
			override val area = "test"
			override val module = "test"
			override val id = "TestEnumeration"
			override val resourcePath = "test/enum"

			override fun getItem(id: String): TestEnum = items.find { it.id == id }!!
		}

	private fun <T : Any> createBaseProperty(
		type: Class<T>,
		initialValue: T? = null,
	): BaseProperty<T> =
		object : BaseProperty<T> {
			override var value: T? = initialValue
			override val type: Class<T> = type
			override val entity: EntityWithProperties get() = rootEntity
			override val relativePath: String = "test"
			override val path: String = "test"
			override val name: String = "test"
			override val isWritable: Boolean = true
		}

	private fun createEnumProperty(
		enumeration: Enumeration<TestEnum>,
		initialValue: TestEnum? = null,
	): EnumProperty<TestEnum> =
		object : EnumProperty<TestEnum> {
			private var _value: TestEnum? = initialValue
			private var _id: String? = initialValue?.id
			private val _idProperty = object : BaseProperty<String> {
				override var value: String?
					get() = _id
					set(v) {
						_id = v
					}
				override val type: Class<String> = String::class.java
				override val entity: EntityWithProperties get() = rootEntity
				override val relativePath: String = "test.id"
				override val path: String = "test.id"
				override val name: String = "id"
				override val isWritable: Boolean = true
			}

			override var value: TestEnum?
				get() = _value
				set(v) {
					_value = v
					_id = v?.id
				}
			override var id: String?
				get() = _id
				set(v) {
					_id = v
					_value = v?.let { enumeration.getItem(it) }
				}
			override val idProperty: BaseProperty<String> = _idProperty
			override val enumeration: Enumeration<TestEnum> = enumeration
			override val type: Class<TestEnum> = TestEnum::class.java
			override val entity: EntityWithProperties get() = rootEntity
			override val relativePath: String = "test"
			override val path: String = "test"
			override val name: String = "test"
			override val isWritable: Boolean = true
		}

	private fun createPartListProperty(): PartListProperty<TestPart> =
		object : PartListProperty<TestPart> {
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
				newPart.addProperty("name", createBaseProperty(String::class.java, "Part$id"))
				val enumeration = createMockEnumeration()
				newPart.addProperty("status", createEnumProperty(enumeration, enumeration.getItem("active")))
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

			override val entity: EntityWithProperties get() = rootEntity
			override val relativePath: String = "test"
			override val path: String = "test"
			override val name: String = "test"
			override val isWritable: Boolean = true
		}

	private fun createReferenceProperty(initialValue: TestAggregate? = null): AggregateReferenceProperty<TestAggregate> =
		object : AggregateReferenceProperty<TestAggregate> {
			private var _value: TestAggregate? = initialValue
			private var _id: Any? = initialValue?.id
			private val _idProperty = object : BaseProperty<Any> {
				override var value: Any?
					get() = _id
					set(v) {
						_id = v
					}
				override val type: Class<Any> = Any::class.java
				override val entity: EntityWithProperties get() = rootEntity
				override val relativePath: String = "test.id"
				override val path: String = "test.id"
				override val name: String = "id"
				override val isWritable: Boolean = true
			}

			override var value: TestAggregate?
				get() = _value
				set(v) {
					_value = v
					_id = v?.id
				}
			override var id: Any?
				get() = _id
				set(v) {
					_id = v
				}
			override val idProperty: BaseProperty<Any> = _idProperty
			override val type: Class<TestAggregate> = TestAggregate::class.java
			override val entity: EntityWithProperties get() = rootEntity
			override val relativePath: String = "test"
			override val path: String = "test"
			override val name: String = "test"
			override val isWritable: Boolean = true
		}

	private fun createPartReferenceProperty(initialValue: TestPart? = null): PartReferenceProperty<TestPart> =
		object : PartReferenceProperty<TestPart> {
			private var _value: TestPart? = initialValue
			private var _id: Int? = initialValue?.id
			private val _idProperty = object : BaseProperty<Int> {
				override var value: Int?
					get() = _id
					set(v) {
						_id = v
					}
				override val type: Class<Int> = Int::class.java
				override val entity: EntityWithProperties get() = rootEntity
				override val relativePath: String = "test.id"
				override val path: String = "test.id"
				override val name: String = "id"
				override val isWritable: Boolean = true
			}

			override var value: TestPart?
				get() = _value
				set(v) {
					_value = v
					_id = v?.id
				}
			override var id: Int?
				get() = _id
				set(v) {
					_id = v
				}
			override val idProperty: BaseProperty<Int> = _idProperty
			override val type: Class<TestPart> = TestPart::class.java
			override val entity: EntityWithProperties get() = rootEntity
			override val relativePath: String = "test"
			override val path: String = "test"
			override val name: String = "test"
			override val isWritable: Boolean = true
		}
}

// Test data classes

data class TestEnum(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	override val enumeration: Enumeration<out Enumerated>
		get() = object : Enumeration<TestEnum> {
			override val items = emptyList<TestEnum>()
			override val area = "test"
			override val module = "test"
			override val id = "TestEnumeration"
			override val resourcePath = "test/enum"

			override fun getItem(id: String): TestEnum = TestEnum(id, id)
		}

	override fun getName(): String = itemName
}

class TestEntity : EntityWithProperties {

	private val _properties = mutableMapOf<String, Property<*>>()
	private var _referenceProperty: AggregateReferenceProperty<TestAggregate>? = null

	fun addProperty(
		name: String,
		property: Property<*>,
	) {
		_properties[name] = property
		if (property is AggregateReferenceProperty<*> && name == "reference") {
			@Suppress("UNCHECKED_CAST")
			_referenceProperty = property as AggregateReferenceProperty<TestAggregate>
		}
	}

	fun setReferenceToNull() {
		_referenceProperty?.value = null
	}

	override val isFrozen: Boolean = false

	override fun hasProperty(name: String): Boolean = _properties.containsKey(name)

	@Suppress("UNCHECKED_CAST")
	override fun <T : Any> getProperty(
		name: String,
		type: KClass<T>,
	): Property<T> = _properties[name] as Property<T>

	override val properties: List<Property<*>> get() = _properties.values.toList()

	override fun hasPart(partId: Int): Boolean = false

	override fun getPart(partId: Int): Part<*> = throw IllegalArgumentException("Part '$partId' not found")
}

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

	override val tenantId: Any get() = 1
	override val tenant: ObjTenant get() = throw NotImplementedError()
	override var owner: ObjUser
		get() = throw NotImplementedError()
		set(value) {}
	override val caption: String = "TestAggregate"
	override val meta: AggregateMeta get() = throw NotImplementedError()
	override val isFrozen: Boolean = false

	override fun hasProperty(name: String): Boolean = _properties.containsKey(name)

	@Suppress("UNCHECKED_CAST")
	override fun <T : Any> getProperty(
		name: String,
		type: KClass<T>,
	): Property<T> = _properties[name] as Property<T>

	override val properties: List<Property<*>> get() = _properties.values.toList()

	override fun hasPart(partId: Int): Boolean = false

	override fun getPart(partId: Int): Part<*> = throw IllegalArgumentException("Part '$partId' not found")
}

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

	override val meta: PartMeta<TestAggregate> get() = throw NotImplementedError()
	override val isFrozen: Boolean = false

	override fun hasProperty(name: String): Boolean = _properties.containsKey(name)

	@Suppress("UNCHECKED_CAST")
	override fun <T : Any> getProperty(
		name: String,
		type: KClass<T>,
	): Property<T> = _properties[name] as Property<T>

	override val properties: List<Property<*>> get() = _properties.values.toList()

	override fun hasPart(partId: Int): Boolean = false

	override fun getPart(partId: Int): Part<*> = throw IllegalArgumentException("Part '$partId' not found")
}
