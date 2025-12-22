package io.dddrive.property.model.impl

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.Part
import io.dddrive.enums.model.Enumerated
import io.dddrive.property.model.AggregateReferenceProperty
import io.dddrive.property.model.BaseProperty
import io.dddrive.property.model.EntityWithProperties
import io.dddrive.property.model.EnumProperty
import io.dddrive.property.model.EnumSetProperty
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.PartReferenceProperty
import io.dddrive.property.model.Property
import io.dddrive.property.model.ReferenceSetProperty
import javassist.util.proxy.MethodHandler
import java.lang.reflect.Method
import java.util.*

class PropertyHandler : MethodHandler {

	@Throws(Throwable::class)
	@Suppress("UNCHECKED_CAST")
	override fun invoke(
		self: Any,
		m: Method,
		proceed: Method?,
		args: Array<Any?>,
	): Any? {
		val methodName = m.name
		try {
			if (this.isCollectionApi(methodName, args)) {
				val fieldName = this.getCollectionFieldName(methodName, args)
				val property = this.getProperty(self, fieldName)
				if (property is PartListProperty<*>) {
					if (args.isEmpty()) {
						if (methodName.startsWith("get") && methodName.endsWith("Count")) {
							return property.partCount
						} else if (methodName.startsWith("get") && methodName.endsWith("List")) {
							return property.parts
						} else if (methodName.startsWith("clear") && methodName.endsWith("List")) {
							property.clearParts()
							return null
						} else if (methodName.startsWith("add")) {
							return property.addPart(null)
						}
					} else if (args.size == 1) {
						if (methodName.startsWith("get") && methodName.endsWith("ById")) {
							return property.getPartById((args[0] as Int?)!!)
						} else if (methodName.startsWith("get")) {
							return property.getPart((args[0] as Int?)!!)
						} else if (methodName.startsWith("remove")) {
							property.removePart((args[0] as Int?)!!)
							return null
						}
					}
				} else if (property is EnumSetProperty<*>) {
					if (args.isEmpty()) {
						if (methodName.startsWith("get") && methodName.endsWith("Count")) {
							return property.items.size
						} else if (methodName.startsWith("get") && methodName.endsWith("Set")) {
							return property.items
						} else if (methodName.startsWith("clear") && methodName.endsWith("Set")) {
							property.clearItems()
							return null
						}
					} else if (args.size == 1) {
						if (methodName.startsWith("has")) {
							return (property as EnumSetProperty<Enumerated>).hasItem((args[0] as Enumerated?)!!)
						} else if (methodName.startsWith("add")) {
							(property as EnumSetProperty<Enumerated>).addItem((args[0] as Enumerated?)!!)
							return null
						} else if (methodName.startsWith("remove")) {
							(property as EnumSetProperty<Enumerated>).removeItem((args[0] as Enumerated?)!!)
							return null
						}
					}
				} else if (property is ReferenceSetProperty<*>) {
					if (args.isEmpty()) {
						if (methodName.startsWith("get") && methodName.endsWith("Count")) {
							return property.items.size
						} else if (methodName.startsWith("get") && methodName.endsWith("Set")) {
							return property.items
						} else if (methodName.startsWith("clear") && methodName.endsWith("Set")) {
							property.clearItems()
							return null
						}
					} else if (args.size == 1) {
						if (methodName.startsWith("has")) {
							return property.hasItem(args[0]!!)
						} else if (methodName.startsWith("add")) {
							property.addItem(args[0]!!)
							return null
						} else if (methodName.startsWith("remove")) {
							property.removeItem(args[0]!!)
							return null
						}
					}
				}
			}
			var fieldName = this.getFieldName(methodName)
			val property = try {
				this.getProperty(self, fieldName)
			} catch (_: NoSuchFieldException) {
				try {
					// try boolean setXyz setter
					fieldName = "is" + fieldName.substring(0, 1).uppercase(Locale.getDefault()) + fieldName.substring(1)
					this.getProperty(self, fieldName)
				} catch (_: NoSuchFieldException) {
					throw NoSuchMethodException(self.javaClass.getSimpleName() + "." + methodName)
				}
			}
			if (this.isGetter(methodName, args)) {
				return if (property is EnumProperty<*>) {
					(property as EnumProperty<Enumerated>).value
				} else if (property is AggregateReferenceProperty<*>) {
					if (m.name.endsWith("Id")) {
						property.id
					} else {
						property.value
					}
				} else if (property is PartReferenceProperty<*>) {
					if (m.name.endsWith("Id")) {
						property.id
					} else {
						property.value
					}
				} else if (property is BaseProperty<*>) { // must be last
					property.value
				} else {
					throw NoSuchFieldException(property.name)
				}
			} else if (this.isSetter(m.name, args)) {
				if (property is EnumProperty<*>) {
					(property as EnumProperty<Enumerated>).value = args[0] as Enumerated?
				} else if (property is AggregateReferenceProperty<*>) {
					if (m.name.endsWith("Id")) {
						(property as AggregateReferenceProperty<Aggregate>).id = args[0]
					} else {
						(property as AggregateReferenceProperty<Aggregate>).value = args[0] as Aggregate?
					}
				} else if (property is PartReferenceProperty<*>) {
					if (m.name.endsWith("Id")) {
						property.id = args[0] as Int?
					} else {
						(property as PartReferenceProperty<Part<*>>).value = args[0] as Part<*>?
					}
				} else if (property is BaseProperty<*>) { // must be last
					(property as BaseProperty<Any?>).value = args[0]
				}
			}
		} catch (_: NoSuchFieldException) {
			throw NoSuchMethodException(self.javaClass.getSimpleName() + "." + methodName)
		}
		return null
	}

	@Throws(NoSuchFieldException::class, IllegalArgumentException::class)
	private fun getProperty(
		obj: Any?,
		fieldName: String,
	): Property<*> {
		val entity = obj as EntityWithProperties
		var property: Property<*>? = if (entity.hasProperty(fieldName)) entity.getProperty(fieldName, Any::class) else null
		if (property != null) {
			return property
		}
		// look for field with underscore prefix (e.g. _name), kotlin style
		property = if (entity.hasProperty("_$fieldName")) entity.getProperty("_$fieldName", Any::class) else null
		if (property != null) {
			return property
		}
		if (fieldName.endsWith("Id")) { // try enumeration / object via field
			return this.getProperty(obj, fieldName.substring(0, fieldName.length - 2))
		} else if (fieldName.endsWith("List")) { // try Set instead of List
			return this.getProperty(obj, fieldName.replace("List", "Set"))
		}
		throw NoSuchFieldException(fieldName)
	}

	@Throws(NoSuchFieldException::class)
	private fun getFieldName(methodName: String): String {
		if (methodName.startsWith("get")) {
			return this.getName(methodName.substring(3))
		} else if (methodName.startsWith("is")) {
			return methodName
		} else if (methodName.startsWith("set")) {
			return this.getName(methodName.substring(3))
		}
		throw NoSuchFieldException(methodName)
	}

	private fun isCollectionApi(
		methodName: String,
		args: Array<Any?>,
	): Boolean {
		if (args.size == 0) {
			if (methodName.startsWith("get") && methodName.endsWith("Count")) {
				return true
			} else if (methodName.startsWith("get") && (methodName.endsWith("List") || methodName.endsWith("Set"))) {
				return true
			} else if (methodName.startsWith("clear") && (methodName.endsWith("List") || methodName.endsWith("Set"))) {
				return true
			} else {
				return methodName.startsWith("add")
			}
		} else if (args.size == 1) {
			if (methodName.startsWith("get") && methodName.endsWith("ById")) {
				return true
			} else if (methodName.startsWith("has")) {
				return true
			} else if (methodName.startsWith("get")) {
				return true
			} else if (methodName.startsWith("add")) {
				return true
			} else {
				return methodName.startsWith("remove")
			}
		}
		return false
	}

	@Throws(NoSuchFieldException::class)
	private fun getCollectionFieldName(
		methodName: String,
		args: Array<Any?>,
	): String {
		if (args.size == 0) {
			if (methodName.startsWith("get") && methodName.endsWith("Count")) {
				return this.getName(methodName.substring(3, methodName.length - 5)) + "List"
			} else if (methodName.startsWith("get") && methodName.endsWith("List")) {
				return this.getName(methodName.substring(3, methodName.length - 4)) + "List"
			} else if (methodName.startsWith("get") && methodName.endsWith("Set")) {
				return this.getName(methodName.substring(3, methodName.length - 3)) + "Set"
			} else if (methodName.startsWith("clear") && methodName.endsWith("List")) {
				return this.getName(methodName.substring(5, methodName.length - 4)) + "List"
			} else if (methodName.startsWith("clear") && methodName.endsWith("Set")) {
				return this.getName(methodName.substring(5, methodName.length - 3)) + "Set"
			} else if (methodName.startsWith("add")) {
				return this.getName(methodName.substring(3)) + "List"
			}
		} else if (args.size == 1) {
			if (methodName.startsWith("get") && methodName.endsWith("ById")) {
				return this.getName(methodName.substring(3, methodName.length - 4)) + "List"
			} else if (methodName.startsWith("has")) {
				return this.getName(methodName.substring(3)) + "Set"
			} else if (methodName.startsWith("get")) {
				return this.getName(methodName.substring(3)) + "List"
			} else if (methodName.startsWith("add")) {
				return this.getName(methodName.substring(3)) + "Set"
			} else if (methodName.startsWith("remove")) {
				return this.getName(methodName.substring(6)) + "List"
			}
		}
		throw NoSuchFieldException(methodName)
	}

	private fun getName(methodName: String): String = methodName.substring(0, 1).lowercase(Locale.getDefault()) + methodName.substring(1)

	private fun isGetter(
		methodName: String,
		args: Array<Any?>,
	): Boolean = (methodName.startsWith("get") || methodName.startsWith("is")) && args.isEmpty()

	private fun isSetter(
		methodName: String,
		args: Array<Any?>,
	): Boolean = methodName.startsWith("set") && args.size == 1

	companion object {

		@JvmField
		val INSTANCE: PropertyHandler = PropertyHandler()
	}
}
