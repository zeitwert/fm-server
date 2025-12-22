
package io.zeitwert.fm.ddd.model;

import java.math.BigDecimal;

import io.dddrive.ddd.model.enums.CodeAggregateType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchResult implements Comparable<SearchResult> {

	private Integer tenantId;
	private CodeAggregateType aggregateType;
	private Integer id;
	private String caption;
	private BigDecimal rank;

	@Override
	public int compareTo(SearchResult other) {
		return this.rank.compareTo(other.getRank());
	}

}
