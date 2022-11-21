
package io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class AggregateDtoBase<A extends Aggregate> {

	public static final String CalculationOnlyOperation = "calculationOnly";
	public static final String DiscardOperation = "discard";

	private static final Map<Class<?>, ?> serviceMap = new HashMap<>();

	protected static final <Aggr extends Aggregate> AggregateRepository<Aggr, ?> getRepository(Class<Aggr> aggrClass) {
		return AppContext.getInstance().getRepository(aggrClass);
	}

	@SuppressWarnings("unchecked")
	protected static final <T> T getService(Class<T> serviceClass) {
		T service = (T) serviceMap.get(serviceClass);
		if (service == null) {
			service = AppContext.getInstance().getBean(serviceClass);
			((Map<Class<T>, T>) ((Object) serviceMap)).put(serviceClass, service);
		}
		return service;
	}

	public abstract AggregateMetaDto getMeta();

	@JsonIgnore
	private A original;

	@JsonApiId
	private Integer id;

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
