
package io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class AggregateDtoBase<A extends Aggregate> {

	public static final String CalculationOnlyOperation = "calculationOnly";
	public static final String DiscardOperation = "discard";

	protected static final <Aggr extends Aggregate> AggregateRepository<Aggr, ?> getRepository(Class<Aggr> aggrClass) {
		return AppContext.getInstance().getRepository(aggrClass);
	}

	protected static final <T> T getService(Class<T> serviceClass) {
		return AppContext.getInstance().getBean(serviceClass);
	}

	public abstract AggregateMetaDto getMeta();

	@JsonIgnore
	private A original;

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
