
package fm.comunas.fm.account.adapter.api.jsonapi.impl;

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
import fm.comunas.ddd.session.model.SessionInfo;
import fm.comunas.fm.account.adapter.api.jsonapi.ObjAccountApiRepository;
import fm.comunas.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import fm.comunas.fm.account.model.ObjAccount;
import fm.comunas.fm.account.model.ObjAccountRepository;
import fm.comunas.fm.account.model.db.tables.records.ObjAccountVRecord;

@Controller("objAccountApiRepository")
public class ObjAccountApiRepositoryImpl extends ResourceRepositoryBase<ObjAccountDto, Integer>
		implements ObjAccountApiRepository {

	private final ObjAccountRepository repository;
	private final SessionInfo sessionInfo;

	@Autowired
	public ObjAccountApiRepositoryImpl(final ObjAccountRepository repository, SessionInfo sessionInfo) {
		super(ObjAccountDto.class);
		this.repository = repository;
		this.sessionInfo = sessionInfo;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ObjAccountDto create(ObjAccountDto dto) {
		if (dto.getId() != null) {
			throw new BadRequestException("Cannot specify id on creation (" + dto.getId() + ")");
		}
		ObjAccount obj = this.repository.create(this.sessionInfo);
		dto.toObj(obj);
		this.repository.store(obj);
		return ObjAccountDto.fromObj(obj, this.sessionInfo);
	}

	@Override
	public ObjAccountDto findOne(Integer objId, QuerySpec querySpec) {
		Optional<ObjAccount> maybeAccount = this.repository.get(this.sessionInfo, objId);
		if (!maybeAccount.isPresent()) {
			throw new ResourceNotFoundException("Resource not found!");
		}
		return ObjAccountDto.fromObj(maybeAccount.get(), this.sessionInfo);
	}

	@Override
	public ResourceList<ObjAccountDto> findAll(QuerySpec querySpec) {
		List<ObjAccountVRecord> itemList = this.repository.find(this.sessionInfo, querySpec);
		ResourceList<ObjAccountDto> list = new DefaultResourceList<>();
		list.addAll(itemList.stream().map(item -> ObjAccountDto.fromRecord(item, this.sessionInfo)).toList());
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ObjAccountDto save(ObjAccountDto dto) {
		if (dto.getId() == null) {
			throw new BadRequestException("Can only save existing object (missing id)");
		}
		ObjAccount obj = this.repository.get(this.sessionInfo, dto.getId()).get();
		dto.toObj(obj);
		this.repository.store(obj);
		return ObjAccountDto.fromObj(obj, this.sessionInfo);
	}

}
