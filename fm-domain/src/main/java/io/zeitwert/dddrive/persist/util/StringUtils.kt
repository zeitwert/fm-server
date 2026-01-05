package io.zeitwert.dddrive.persist.util

import java.util.*

object StringUtils {

	fun toCamelCase(name: String): String {
		val parts = name.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		val camelCaseString = StringBuilder()
		for (part in parts) {
			camelCaseString.append(toProperCase(part))
		}
		return camelCaseString.substring(0, 1).lowercase(Locale.getDefault()) + camelCaseString.substring(1)
	}

	fun toProperCase(name: String): String = name.substring(0, 1).uppercase(Locale.getDefault()) + name.substring(1).lowercase(Locale.getDefault())

	fun toSnakeCase(name: String): String = name.replace("(.)(\\p{Upper})".toRegex(), "$1_$2").lowercase(Locale.getDefault())

}
