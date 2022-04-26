package io.zeitwert.ddd.doc.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiMetaInformation;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.base.DocFields;
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
@NoArgsConstructor
@SuperBuilder
public abstract class DocDtoBase<D extends Doc> {

	private static final ObjUserRepository userRepository = (ObjUserRepository) AppContext.getInstance()
			.getRepository(ObjUser.class);

	@JsonIgnore
	protected SessionInfo sessionInfo;

	@JsonApiMetaInformation
	private DocMetaDto meta;

	@JsonIgnore
	private D original;

	@JsonApiId
	private Integer id;

	private String caption;

	private ObjUserDto owner;

	@JsonApiRelation
	public List<DocPartTransitionDto> getTransitions() {
		return this.original.getMeta().getTransitionList().stream().map(t -> DocPartTransitionDto.fromPart(t)).toList();
	}

	public void toDoc(D doc) {
		doc.setOwner(this.getOwner() != null ? userRepository.get(this.getOwner().getId()) : null);
	}

	public static void fromDoc(DocDtoBaseBuilder<?, ?, ?> dtoBuilder, Doc doc, SessionInfo sessionInfo) {
		// @formatter:off
		dtoBuilder
			.sessionInfo(sessionInfo)
			.meta(DocMetaDto.fromDoc(doc, sessionInfo))
			.id(doc.getId())
			.caption(doc.getCaption())
			.owner(ObjUserDto.fromObj(doc.getOwner()));
		// @formatter:on
	}

	public static void fromRecord(DocDtoBaseBuilder<?, ?, ?> dtoBuilder, Record doc, SessionInfo sessionInfo) {
		// @formatter:off
		dtoBuilder
			.sessionInfo(sessionInfo)
			//.meta(DocMetaDto.fromDoc(doc, sessionInfo))
			.id(doc.get(DocFields.ID))
			.caption(doc.get(DocFields.CAPTION));
			//.owner(ObjUserDto.fromObj(doc.getOwner()))
		// @formatter:on
	}

}
