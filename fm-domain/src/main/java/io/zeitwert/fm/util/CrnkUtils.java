package io.zeitwert.fm.util;

import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.QuerySpec;

public class CrnkUtils {

	public static boolean hasFilterFor(QuerySpec querySpec, String fieldName) {
		return querySpec.getFilters().stream().anyMatch(f -> getPath(f).equals(fieldName));
	}

	public static String getPath(FilterSpec filter) {
		return String.join(".", filter.getPath().getElements()).replace(".id", "Id");
	}

}
