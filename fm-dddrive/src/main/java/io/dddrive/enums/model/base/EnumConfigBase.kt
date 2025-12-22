package io.dddrive.enums.model.base

import java.util.concurrent.atomic.AtomicInteger
import java.util.function.IntUnaryOperator

abstract class EnumConfigBase {

	protected fun startConfig() {
		configCounter.incrementAndGet()
	}

	protected fun endConfig() {
		configCounter.getAndUpdate(
			IntUnaryOperator { counter: Int ->
				if (counter == 1) {
					assignEnumItems()
				}
				counter - 1
			},
		)
	}

	companion object {

		private val configCounter = AtomicInteger(0)
		private val enumerations: MutableSet<EnumerationBase<*>> = HashSet<EnumerationBase<*>>()

		fun isInConfig(): Boolean = configCounter.get() > 0

		fun addEnum(enumeration: EnumerationBase<*>?) {
			enumerations.add(enumeration!!)
		}

		fun assignEnumItems() {
			for (enumeration in enumerations) {
				enumeration.assignItems()
			}
			enumerations.clear()
		}
	}

}
