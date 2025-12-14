package io.zeitwert.fm.task.adapter.api.jsonapi.impl;

import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.doc.adapter.api.jsonapi.base.DocDtoAdapterBase;
import io.zeitwert.fm.task.adapter.api.jsonapi.dto.DocTaskDto;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;
import io.zeitwert.fm.task.model.enums.CodeTaskPriority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;

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

	public ObjAccountDto getAccountDto(Integer id) {
		return id != null ? this.accountDtoAdapter.fromAggregate(this.accountRepository.get(id)) : null;
	}

	@Override
	public void toAggregate(DocTaskDto dto, DocTask doc) {
		Integer relatedToId = dto.getRelatedTo() != null ? Integer.parseInt(dto.getRelatedTo().getId()) : null;
		super.toAggregate(dto, doc);
		doc.setAccountId(dto.getAccountId());
		doc.setRelatedToId(relatedToId);
		doc.setSubject(dto.getSubject());
		doc.setContent(dto.getContent());
		doc.setIsPrivate(dto.getIsPrivate());
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
				.accountId((Integer)doc.getAccountId())
				.relatedTo(this.asEnumerated(doc.getRelatedTo()))
				.subject(doc.getSubject())
				.content(doc.getContent())
				.isPrivate(doc.getIsPrivate())
				.priority(EnumeratedDto.of(doc.getPriority()))
				.dueAt(doc.getDueAt())
				.remindAt(doc.getRemindAt())
				.build();
	}

//	@Override
//	public DocTaskDto fromRecord(DocTaskVRecord doc) {
//		if (doc == null) {
//			return null;
//		}
//		DocTaskDto.DocTaskDtoBuilder<?, ?> dtoBuilder = DocTaskDto.builder();
//		this.fromRecord(dtoBuilder, doc);
//		Integer relatedToId = doc.getRelatedObjId() != null ? doc.getRelatedObjId() : doc.getRelatedDocId();
//		EnumeratedDto relatedTo = EnumeratedDto.builder().id(relatedToId.toString()).build();
//		return dtoBuilder
//				.accountId(doc.getAccountId())
//				.relatedTo(relatedTo)
//				.subject(doc.getSubject())
//				.content(doc.getContent())
//				.isPrivate(doc.getIsPrivate())
//				.priority(EnumeratedDto.of(CodeTaskPriority.getPriority(doc.getPriorityId())))
//				.dueAt(doc.getDueAt())
//				.remindAt(doc.getRemindAt())
//				.build();
//	}

}
