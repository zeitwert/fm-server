package io.zeitwert.ddd.doc.adapter.api.jsonapi.dto;

import java.time.OffsetDateTime;
import java.util.List;

import io.crnk.core.resource.meta.MetaInformation;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocMeta;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.ddd.validation.adapter.api.jsonapi.dto.AggregatePartValidationDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocMetaDto implements MetaInformation {

	private Integer sessionId;
	private EnumeratedDto itemType;
	private ObjTenantDto tenant;
	private ObjUserDto owner;
	private ObjUserDto createdByUser;
	private OffsetDateTime createdAt;
	private ObjUserDto modifiedByUser;
	private OffsetDateTime modifiedAt;
	private List<CodeCaseStage> caseStages;
	private List<String> availableActions;
	private List<DocPartTransitionDto> transitionList;
	private List<AggregatePartValidationDto> validationList;

	public static DocMetaDto fromDoc(Doc doc, SessionInfo sessionInfo) {
		DocMeta meta = doc.getMeta();
		// @formatter:off
		return DocMetaDto.builder()
			.sessionId(sessionInfo.getId())
			.itemType(EnumeratedDto.fromEnum(doc.getRepository().getAggregateType()))
			.tenant(ObjTenantDto.fromObj(doc.getTenant()))
			.owner(ObjUserDto.fromObj(doc.getOwner()))
			.createdByUser(ObjUserDto.fromObj(meta.getCreatedByUser()))
			.createdAt(meta.getCreatedAt())
			.modifiedByUser(ObjUserDto.fromObj(meta.getModifiedByUser()))
			.modifiedAt(meta.getModifiedAt())
			.transitionList(meta.getTransitionList().stream().map(v -> DocPartTransitionDto.fromPart(v)).toList())
			.validationList(meta.getValidationList().stream().map(v -> AggregatePartValidationDto.fromValidation(v)).toList())
			.build();
		// @formatter:on
	}

}
