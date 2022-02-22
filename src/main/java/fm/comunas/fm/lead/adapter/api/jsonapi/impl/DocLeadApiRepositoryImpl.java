
package fm.comunas.fm.lead.adapter.api.jsonapi.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import fm.comunas.fm.lead.adapter.api.jsonapi.DocLeadApiRepository;
import fm.comunas.fm.lead.adapter.api.jsonapi.dto.DocLeadDto;
import fm.comunas.fm.lead.model.DocLead;
import fm.comunas.fm.lead.model.DocLeadRepository;
import fm.comunas.fm.lead.model.db.tables.records.DocLeadVRecord;
import fm.comunas.ddd.session.model.SessionInfo;

@Controller("docLeadApiRepository")
public class DocLeadApiRepositoryImpl extends ResourceRepositoryBase<DocLeadDto, Integer>
		implements DocLeadApiRepository {

	private final DocLeadRepository repository;
	private final SessionInfo sessionInfo;

	@Autowired
	public DocLeadApiRepositoryImpl(final DocLeadRepository repository, SessionInfo sessionInfo) {
		super(DocLeadDto.class);
		this.repository = repository;
		this.sessionInfo = sessionInfo;
	}

	@Override
	@SuppressWarnings("unchecked")
	public DocLeadDto create(DocLeadDto dto) {
		if (dto.getId() != null) {
			throw new BadRequestException("Cannot specify id on creation (" + dto.getId() + ")");
		}
		DocLead doc = this.repository.create(this.sessionInfo);
		dto.toDoc(doc);
		this.repository.store(doc);
		return DocLeadDto.fromDoc(doc, this.sessionInfo);
	}

	@Override
	public DocLeadDto findOne(Integer docId, QuerySpec querySpec) {
		Optional<DocLead> maybeLead = this.repository.get(this.sessionInfo, docId);
		if (!maybeLead.isPresent()) {
			throw new ResourceNotFoundException("Resource not found!");
		}
		return DocLeadDto.fromDoc(maybeLead.get(), this.sessionInfo);
	}

	@Override
	public ResourceList<DocLeadDto> findAll(QuerySpec querySpec) {
		List<DocLeadVRecord> itemList = this.repository.find(this.sessionInfo, querySpec);
		ResourceList<DocLeadDto> list = new DefaultResourceList<>();
		list.addAll(itemList.stream().map(item -> DocLeadDto.fromRecord(item, this.sessionInfo)).toList());
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public DocLeadDto save(DocLeadDto dto) {
		if (dto.getId() == null) {
			throw new BadRequestException("Can only save existing object (missing id)");
		}
		DocLead doc = this.repository.get(this.sessionInfo, dto.getId()).get();
		dto.toDoc(doc);
		this.repository.store(doc);
		return DocLeadDto.fromDoc(doc, this.sessionInfo);
	}

}
