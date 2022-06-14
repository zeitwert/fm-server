package io.zeitwert.ddd.doc.adapter.api.jsonapi.dto;

import java.time.OffsetDateTime;
import java.util.List;

import io.crnk.core.resource.meta.MetaInformation;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocMeta;
import io.zeitwert.ddd.doc.model.base.DocFields;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.ddd.validation.adapter.api.jsonapi.dto.AggregatePartValidationDto;
import lombok.Builder;
import lombok.Data;
import org.jooq.Record;

@Data
@Builder
public class DocMetaDto implements MetaInformation {

	private Integer sessionId;
	private EnumeratedDto itemType;
	private ObjTenantDto tenant;
	private ObjUserDto owner;
	private EnumeratedDto caseStage;
	private List<EnumeratedDto> caseStages; // TODO implement
	private List<String> availableActions; // TODO implement
	private ObjUserDto createdByUser;
	private OffsetDateTime createdAt;
	private ObjUserDto modifiedByUser;
	private OffsetDateTime modifiedAt;
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
			.caseStage(EnumeratedDto.fromEnum(doc.getCaseStage()))
			.createdByUser(ObjUserDto.fromObj(meta.getCreatedByUser()))
			.createdAt(meta.getCreatedAt())
			.modifiedByUser(ObjUserDto.fromObj(meta.getModifiedByUser()))
			.modifiedAt(meta.getModifiedAt())
			.transitionList(meta.getTransitionList().stream().map(v -> DocPartTransitionDto.fromPart(v)).toList())
			.validationList(meta.getValidationList().stream().map(v -> AggregatePartValidationDto.fromValidation(v)).toList())
			.build();
		// @formatter:on
	}

	public static DocMetaDto fromRecord(Record doc, SessionInfo sessionInfo) {
		ObjTenantRepository tenantRepo = (ObjTenantRepository) AppContext.getInstance().getRepository(ObjTenant.class);
		ObjUserRepository userRepo = (ObjUserRepository) AppContext.getInstance().getRepository(ObjUser.class);
		Integer modifiedByUserId = doc.getValue(DocFields.MODIFIED_BY_USER_ID);
		ObjUserDto modifiedByUser = modifiedByUserId == null ? null : ObjUserDto.fromObj(userRepo.get(modifiedByUserId));
		// @formatter:off
		return DocMetaDto.builder()
			.sessionId(sessionInfo.getId())
			.itemType(EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType(doc.get(DocFields.DOC_TYPE_ID))))
			.tenant(ObjTenantDto.fromObj(tenantRepo.get(doc.getValue(DocFields.TENANT_ID))))
			.owner(ObjUserDto.fromObj(userRepo.get(doc.getValue(DocFields.OWNER_ID))))
			.caseStage(EnumeratedDto.fromEnum(CodeCaseStageEnum.getCaseStage(doc.get(DocFields.CASE_STAGE_ID))))
			.createdByUser(ObjUserDto.fromObj(userRepo.get(doc.getValue(DocFields.CREATED_BY_USER_ID))))
			.createdAt(doc.get(DocFields.CREATED_AT))
			.modifiedByUser(modifiedByUser)
			.modifiedAt(doc.get(DocFields.MODIFIED_AT))
			.build();
		// @formatter:on
	}

}
