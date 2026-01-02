package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dddrive.ddd.core.model.Aggregate;
import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDto;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDtoAdapter;
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
	@JsonApiId
	private Integer id;
	private String caption;
	// Read: for orderbooks, write: for creation
	private EnumeratedDto tenant;
	// Read: for orderbooks, write: for updates
	private EnumeratedDto owner;

	public AggregateDtoAdapter<?, ?> getAdapter() {
		return this.adapter;
	}

	public abstract AggregateMetaDto getMeta();

	// For explicit filtering in SaaS session
	@JsonApiField(readable = false, filterable = true)
	public Integer getTenantId() {
		return null;
	}

	@JsonApiField(readable = false, filterable = true)
	public String getSearchText() {
		return null;
	}

}
