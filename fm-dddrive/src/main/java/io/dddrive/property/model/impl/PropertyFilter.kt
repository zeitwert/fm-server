package io.dddrive.property.model.impl

import javassist.util.proxy.MethodFilter
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class PropertyFilter : MethodFilter {

	override fun isHandled(method: Method): Boolean = Modifier.isAbstract(method.modifiers)

	companion object {

		@JvmField
		val INSTANCE: PropertyFilter = PropertyFilter()
	}

}
