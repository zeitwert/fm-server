package io.zeitwert.fm.task.adapter.api.jsonapi.dto;

// TODO-MIGRATION: REST-API - remove after Phase 3 (REST API migration)
// This JSON:API DTO uses Crnk framework. Will be replaced with REST response DTO in Phase 3.

/*
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.doc.adapter.api.jsonapi.dto.DocDtoBase;
import io.zeitwert.fm.task.adapter.api.jsonapi.impl.DocTaskDtoAdapter;
import io.zeitwert.fm.task.model.DocTask;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "task", resourcePath = "collaboration/tasks")
public class DocTaskDto extends DocDtoBase<DocTask> {

	@Override
	public DocTaskDtoAdapter getAdapter() {
		return (DocTaskDtoAdapter) super.getAdapter();
	}

	private EnumeratedDto relatedTo;

	@JsonApiField(readable = false, filterable = true)
	public Integer getRelatedToId() {
		return null;
	}

	@JsonApiRelationId
	private Integer accountId;

	@JsonIgnore
	private ObjAccountDto accountDto;

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
		this.accountDto = null;
	}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjAccountDto getAccount() {
		if (this.accountDto == null) {
			this.accountDto = this.getAdapter().getAccountDto(this.accountId);
		}
		return this.accountDto;
	}

	public void setAccount(ObjAccountDto account) {
		this.accountDto = account;
		this.accountId = account != null ? account.getId() : null;
	}

	private String subject;
	private String content;
	private Boolean isPrivate;

	private EnumeratedDto priority;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private OffsetDateTime dueAt;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private OffsetDateTime remindAt;

}
*/
