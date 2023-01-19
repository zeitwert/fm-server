package io.zeitwert.fm.task.adapter.api.jsonapi.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.doc.adapter.api.jsonapi.dto.FMDocDtoBase;
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
public class DocTaskDto extends FMDocDtoBase<DocTask> {

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
			ObjAccount account = null;
			if (this.getOriginal() != null) {
				account = this.getOriginal().getAccount();
			} else if (this.accountId != null) {
				account = getService(ObjAccountCache.class).get(this.accountId);
			}
			this.accountDto = ObjAccountDtoAdapter.getInstance().fromAggregate(account);
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
