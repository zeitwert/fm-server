package io.zeitwert.fm.task.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.DtoUtils;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.doc.adapter.api.jsonapi.base.DocDtoAdapterBase;
import io.zeitwert.fm.task.adapter.api.jsonapi.dto.DocTaskDto;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.enums.CodeTaskPriority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("docTaskDtoAdapter")
public class DocTaskDtoAdapter extends DocDtoAdapterBase<DocTask, DocTaskDto> {

	private ObjAccountRepository accountRepository = null;
	private ObjAccountDtoAdapter accountDtoAdapter;

	@Autowired
	void setAccountRepository(ObjAccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Autowired
	void setAccountDtoAdapter(ObjAccountDtoAdapter accountDtoAdapter) {
		this.accountDtoAdapter = accountDtoAdapter;
	}

	public ObjAccountDto getAccountDto(String id) {
		return id != null ? this.accountDtoAdapter.fromAggregate(this.accountRepository.get(DtoUtils.idFromString(id))) : null;
	}

	@Override
	public void toAggregate(DocTaskDto dto, DocTask doc) {
		Object relatedToId = dto.getRelatedTo() != null ? DtoUtils.idFromString(dto.getRelatedTo().getId()) : null;
		super.toAggregate(dto, doc);
		doc.setAccountId(DtoUtils.idFromString(dto.getAccountId()));
		doc.setRelatedToId(relatedToId);
		doc.setSubject(dto.getSubject());
		doc.setContent(dto.getContent());
		doc.setPrivate(dto.getIsPrivate());
		doc.setPriority(dto.getPriority() == null ? null : CodeTaskPriority.getPriority(dto.getPriority().getId()));
		doc.setDueAt(dto.getDueAt());
		doc.setRemindAt(dto.getRemindAt());
	}

	@Override
	public DocTaskDto fromAggregate(DocTask doc) {
		if (doc == null) {
			return null;
		}
		DocTaskDto.DocTaskDtoBuilder<?, ?> dtoBuilder = DocTaskDto.builder();
		this.fromAggregate(dtoBuilder, doc);
		return dtoBuilder
				.accountId(doc.getAccountId() != null ? DtoUtils.idToString(doc.getAccountId()) : null)
				.relatedTo(this.asEnumerated(doc.getRelatedTo()))
				.subject(doc.getSubject())
				.content(doc.getContent())
				.isPrivate(doc.isPrivate())
				.priority(EnumeratedDto.of(doc.getPriority()))
				.dueAt(doc.getDueAt())
				.remindAt(doc.getRemindAt())
				.build();
	}

}
