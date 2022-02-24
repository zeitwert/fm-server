
package io.zeitwert.ddd.obj.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiMetaInformation;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.base.ObjFields;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.session.model.SessionInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.jooq.Record;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class ObjDtoBase<O extends Obj> {

	private static final ObjUserRepository userRepository = (ObjUserRepository) AppContext.getInstance()
			.getRepository(ObjUser.class);

	protected <A extends Aggregate> AggregateRepository<A, ?> getRepository(Class<A> aggrClass) {
		return AppContext.getInstance().getRepository(aggrClass);
	}

	@JsonIgnore
	protected SessionInfo sessionInfo;

	@JsonApiMetaInformation
	private ObjMetaDto meta;

	@JsonIgnore
	private O original;

	@JsonApiId
	private Integer id;

	@JsonApiField(readable = false, filterable = true)
	public String getSearchText() {
		return null;
	}

	private String caption;

	private ObjUserDto owner;

	@JsonApiRelation
	public List<ObjPartTransitionDto> getTransitions() {
		return this.original.getMeta().getTransitionList().stream().map(t -> ObjPartTransitionDto.fromPart(t)).toList();
	}

	public void toObj(O obj) {
		obj.setOwner(this.getOwner() != null ? userRepository.get(this.getOwner().getId()).get() : null);
	}

	public static void fromObj(ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, Obj obj, SessionInfo sessionInfo) {
		// @formatter:off
		dtoBuilder
			.sessionInfo(sessionInfo)
			.meta(ObjMetaDto.fromObj(obj, sessionInfo))
			.id(obj.getId())
			.caption(obj.getCaption())
			.owner(ObjUserDto.fromObj(obj.getOwner()));
		// @formatter:on
	}

	public static void fromRecord(ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, Record obj, SessionInfo sessionInfo) {
		// @formatter:off
		dtoBuilder
			.sessionInfo(sessionInfo)
			.meta(ObjMetaDto.fromRecord(obj, sessionInfo))
			.id(obj.get(ObjFields.ID))
			.caption(obj.get(ObjFields.CAPTION))
			.owner(ObjUserDto.fromObj(userRepository.get(obj.get(ObjFields.OWNER_ID)).get()));
		// @formatter:on
	}

}
