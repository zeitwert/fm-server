
package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDto;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDtoAdapter;
import io.dddrive.core.ddd.model.Aggregate;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class AggregateDtoBase<A extends Aggregate> implements AggregateDto<A> {

	public static final String CalculationOnlyOperation = "calculationOnly";

	@JsonIgnore
	private AggregateDtoAdapter<?, ?> adapter;

	@Override
	public AggregateDtoAdapter<?, ?> getAdapter() {
		return this.adapter;
	}

	public abstract AggregateMetaDto getMeta();

	@JsonApiId
	private Integer id;

	// For explicit filtering in SaaS session
	@JsonApiField(readable = false, filterable = true)
	public Integer getTenantId() {
		return null;
	}

	@JsonApiField(readable = false, filterable = true)
	public String getSearchText() {
		return null;
	}

	private String caption;

	// Read: for orderbooks, write: for creation
	private EnumeratedDto tenant;

	// Read: for orderbooks, write: for updates
	private EnumeratedDto owner;

}
