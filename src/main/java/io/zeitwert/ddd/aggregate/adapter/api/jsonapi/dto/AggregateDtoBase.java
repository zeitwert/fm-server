
package io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.session.model.SessionInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class AggregateDtoBase<A extends Aggregate> {

	protected static final <Aggr extends Aggregate> AggregateRepository<Aggr, ?> getRepository(Class<Aggr> aggrClass) {
		return AppContext.getInstance().getRepository(aggrClass);
	}

	@JsonIgnore
	protected SessionInfo sessionInfo;

	@JsonIgnore
	private A original;

	@JsonApiId
	private Integer id;

	@JsonApiField(readable = false, filterable = true)
	public String getSearchText() {
		return null;
	}

	private String caption;

	private ObjUserDto owner;

}
