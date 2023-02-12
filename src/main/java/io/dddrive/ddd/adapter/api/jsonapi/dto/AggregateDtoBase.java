
package io.dddrive.ddd.adapter.api.jsonapi.dto;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.dddrive.app.service.api.AppContext;
import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.service.api.AggregateCache;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class AggregateDtoBase<A extends Aggregate> {

	public static final String CalculationOnlyOperation = "calculationOnly";
	public static final String DiscardOperation = "discard";

	@JsonIgnore
	private AppContext appContext;

	protected final <Aggr extends Aggregate> AggregateCache<Aggr> getCache(Class<Aggr> aggrClass) {
		return this.appContext.getCache(aggrClass);
	}

	protected final <T extends AggregateDtoAdapterBase<?, ?, ?>> T getAdapter(Class<T> adapterClass) {
		return this.appContext.getBean(adapterClass);
	}

	@Autowired
	public final void setAppContext(AppContext appContext) {
		System.out.println("AggregateDtoBase.setAppContext");
		this.appContext = appContext;
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
