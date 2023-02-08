
package io.zeitwert.fm.task.adapter.api.jsonapi.impl;

import io.zeitwert.fm.doc.adapter.api.jsonapi.base.DocDtoAdapterBase;
import io.zeitwert.fm.task.adapter.api.jsonapi.dto.DocTaskDto;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;
import io.zeitwert.fm.task.model.enums.CodeTaskPriorityEnum;

import static io.zeitwert.ddd.util.Check.assertThis;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.model.ObjRepository;

public final class DocTaskDtoAdapter extends DocDtoAdapterBase<DocTask, DocTaskVRecord, DocTaskDto> {

	private static DocTaskDtoAdapter instance;

	private DocTaskDtoAdapter() {
	}

	public static final DocTaskDtoAdapter getInstance() {
		if (instance == null) {
			instance = new DocTaskDtoAdapter();
		}
		return instance;
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
		doc.setPriority(dto.getPriority() == null ? null : CodeTaskPriorityEnum.getPriority(dto.getPriority().getId()));
		doc.setDueAt(dto.getDueAt());
		doc.setRemindAt(dto.getRemindAt());
	}

	@Override
	public DocTaskDto fromAggregate(DocTask doc) {
		if (doc == null) {
			return null;
		}
		DocTaskDto.DocTaskDtoBuilder<?, ?> dtoBuilder = DocTaskDto.builder().original(doc);
		this.fromAggregate(dtoBuilder, doc);
		return dtoBuilder
				.accountId(doc.getAccountId())
				.relatedTo(this.asEnumerated(doc.getRelatedTo()))
				.subject(doc.getSubject())
				.content(doc.getContent())
				.isPrivate(doc.getIsPrivate())
				.priority(EnumeratedDto.fromEnum(doc.getPriority()))
				.dueAt(doc.getDueAt())
				.remindAt(doc.getRemindAt())
				.build();
	}

	@Override
	public DocTaskDto fromRecord(DocTaskVRecord doc) {
		if (doc == null) {
			return null;
		}
		DocTaskDto.DocTaskDtoBuilder<?, ?> dtoBuilder = DocTaskDto.builder().original(null);
		this.fromRecord(dtoBuilder, doc);
		Integer relatedToId = doc.getRelatedObjId() != null ? doc.getRelatedObjId() : doc.getRelatedDocId();
		assertThis(ObjRepository.isObjId(relatedToId), "relatedToId is obj (doc nyi)");
		EnumeratedDto relatedTo = EnumeratedDto.builder().id(relatedToId.toString()).build();
		return dtoBuilder
				.accountId(doc.getAccountId())
				.relatedTo(relatedTo)
				.subject(doc.getSubject())
				.content(doc.getContent())
				.isPrivate(doc.getIsPrivate())
				.priority(EnumeratedDto.fromEnum(CodeTaskPriorityEnum.getPriority(doc.getPriorityId())))
				.dueAt(doc.getDueAt())
				.remindAt(doc.getRemindAt())
				.build();
	}

}
