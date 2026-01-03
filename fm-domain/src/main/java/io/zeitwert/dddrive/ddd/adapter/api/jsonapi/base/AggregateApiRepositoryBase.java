package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base;

import dddrive.app.doc.model.Doc;
import dddrive.app.obj.model.Obj;
import dddrive.app.obj.model.ObjRepository;
import dddrive.ddd.core.model.Aggregate;
import dddrive.ddd.core.model.AggregateRepository;
import io.crnk.core.engine.document.ErrorData;
import io.crnk.core.engine.document.ErrorDataBuilder;
import io.crnk.core.engine.http.HttpStatus;
import io.crnk.core.exception.BadRequestException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDtoAdapter;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.AggregateDtoBase;
import io.zeitwert.dddrive.model.FMAggregateRepository;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class AggregateApiRepositoryBase<A extends Aggregate, D extends AggregateDtoBase<A>>
		extends ResourceRepositoryBase<D, Integer> {

	private final SessionContext sessionCtx;
	private final ObjUserRepository userRepository;
	private final AggregateRepository<A> repository;
	private final AggregateDtoAdapter<A, D> dtoAdapter;

	public AggregateApiRepositoryBase(
			Class<D> dtoClass,
			SessionContext sessionCtx,
			ObjUserRepository userRepository,
			AggregateRepository<A> repository,
			AggregateDtoAdapter<A, D> dtoAdapter) {
		super(dtoClass);
		this.sessionCtx = sessionCtx;
		this.userRepository = userRepository;
		this.repository = repository;
		this.dtoAdapter = dtoAdapter;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public <S extends D> S create(S dto) {
		if (dto.getId() != null) {
			throw new BadRequestException("Cannot specify id on creation (" + dto.getId() + ")");
		}
		try {
			// userRepository.touch(sessionCtx.getUser().getId());
			A aggregate = repository.create();
			dtoAdapter.toAggregate(dto, aggregate);
			repository.store(aggregate);
			return (S) dtoAdapter.fromAggregate(aggregate);
		} catch (Exception x) {
			throw new RuntimeException("crashed on create", x);
		}
	}

	@Override
	@Transactional
	public D findOne(Integer objId, QuerySpec querySpec) {
		try {
			// userRepository.touch(sessionCtx.getUser().getId());
			A aggregate = repository.load(objId);
			sessionCtx.addAggregate(objId, aggregate);
			return dtoAdapter.fromAggregate(aggregate);
		} catch (Exception x) {
			x.printStackTrace();
			throw new ResourceNotFoundException(repository.getAggregateType().getDefaultName() + "[" + objId + "]");
		}
	}

	@Override
	@Transactional
	public ResourceList<D> findAll(QuerySpec querySpec) {
		try {
			// userRepository.touch(sessionCtx.getUser().getId());
			List<Object> itemList = ((FMAggregateRepository) repository).find(null);
			// List<A> itemList = repository.find(querySpec);
			ResourceList<D> list = new DefaultResourceList<>();
			list.addAll(itemList.stream().map(repository::get).map(dtoAdapter::fromAggregate).toList());
			return list;
		} catch (Exception x) {
			throw new RuntimeException("crashed on findAll", x);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public <S extends D> S save(S dto) {
		// userRepository.touch(sessionCtx.getUser().getId());
		if (dto.getId() == null) {
			throw new BadRequestException("Can only save existing object (missing id)");
		} else if (dto.getMeta() == null) {
			throw new BadRequestException("Missing meta information (version or operation)");
		} else if (dto.getMeta().getClientVersion() == null
				&& !dto.getMeta().hasOperation(AggregateDtoBase.CalculationOnlyOperation)) {
			throw new BadRequestException("Missing meta information (version or operation)");
		}
		try {
			A aggregate = sessionCtx.hasAggregate(dto.getId())
					? (A) sessionCtx.getAggregate(dto.getId())
					: repository.load(dto.getId());
			if (dto.getMeta().hasOperation(AggregateDtoBase.CalculationOnlyOperation)) {
				dtoAdapter.toAggregate(dto, aggregate);
			} else {
				if (dto.getMeta().getClientVersion() == null) {
					throw new BadRequestException("Missing version");
				} else if (dto.getMeta().getClientVersion() != aggregate.getMeta().getVersion()) {
					Object userId = null;
					if (aggregate instanceof Obj) {
						userId = userRepository.get(((Obj) aggregate).getMeta().getModifiedByUserId()).getCaption();
					} else {
						userId = userRepository.get(((Doc) aggregate).getMeta().getModifiedByUserId()).getCaption();
					}
					ErrorData errorData = new ErrorDataBuilder()
							.setStatus("" + HttpStatus.CONFLICT_409)
							.setTitle("Fehler beim Speichern")
							.setDetail("Sie versuchten eine veraltete Version zu speichern."
									+ " Benutzer " + userRepository.get(userId).getCaption()
									+ " hat das Objekt in der Zwischenzeit bereits geändert."
									+ " Ihre Änderungen wurden verworfen und die aktuelle Version geladen.")
							.build();
					throw new BadRequestException(HttpStatus.CONFLICT_409, errorData);
				}
				dtoAdapter.toAggregate(dto, aggregate);
				repository.store(aggregate);
				aggregate = repository.get(dto.getId());
			}
			return (S) dtoAdapter.fromAggregate(aggregate);
		} catch (Exception x) {
			throw new RuntimeException("crashed on save", x);
		}
	}

	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public void delete(Integer id) {
		// userRepository.touch(sessionCtx.getUser().getId());
		if (id == null) {
			throw new ResourceNotFoundException("Can only delete existing object (missing id)");
		}
		try {
			A aggregate = sessionCtx.hasAggregate(id)
					? (A) sessionCtx.getAggregate(id)
					: repository.load(id);
			if (!(aggregate instanceof Obj)) {
				throw new BadRequestException("Can only delete an Object");
			}
			((ObjRepository<Obj>) repository).close((Obj) aggregate);
			repository.store(aggregate);
		} catch (Exception x) {
			throw new RuntimeException("crashed on delete", x);
		}
	}

}
