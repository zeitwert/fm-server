
package io.dddrive.util;

public class StringUtils {

	public static String toCamelCase(String name) {
		String[] parts = name.split("_");
		String camelCaseString = "";
		for (String part : parts) {
			camelCaseString = camelCaseString + StringUtils.toProperCase(part);
		}
		return camelCaseString.substring(0, 1).toLowerCase() + camelCaseString.substring(1);
	}

	public static String toProperCase(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
	}

	public static String toSnakeCase(String name) {
		return name.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
	}

}
