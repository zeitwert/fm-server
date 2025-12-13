
package io.zeitwert.fm.util;

import io.crnk.core.queryspec.FilterOperator;

public class CustomFilters {

	public static FilterOperator IN = new FilterOperator("IN") {
		@Override
		public boolean matches(Object value1, Object value2) {
			throw new UnsupportedOperationException(); // handle differently
		}
	};

}
