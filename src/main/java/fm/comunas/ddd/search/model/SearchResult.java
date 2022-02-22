
package fm.comunas.ddd.search.model;

import java.math.BigDecimal;

import fm.comunas.ddd.aggregate.model.enums.CodeAggregateType;
import fm.comunas.ddd.oe.model.ObjTenant;

public class SearchResult implements Comparable<SearchResult> {

	private ObjTenant tenant;
	private CodeAggregateType aggregateType;
	private Integer id;
	private String caption;
	private BigDecimal rank;

	public SearchResult(ObjTenant tenant, CodeAggregateType aggregateType, Integer id, String caption, BigDecimal rank) {
		this.tenant = tenant;
		this.aggregateType = aggregateType;
		this.id = id;
		this.caption = caption;
		this.rank = rank;
	}

	public ObjTenant getTenant() {
		return this.tenant;
	}

	public CodeAggregateType getAggregateType() {
		return this.aggregateType;
	}

	public String getId() {
		return String.valueOf(this.id);
	}

	public String getCaption() {
		return this.caption;
	}

	public BigDecimal getRank() {
		return this.rank;
	}

	@Override
	public int compareTo(SearchResult other) {
		return this.rank.compareTo(other.getRank());
	}

}
