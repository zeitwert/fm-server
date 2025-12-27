package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base;

import io.crnk.core.engine.document.ErrorData;
import io.crnk.core.engine.document.ErrorDataBuilder;
import io.crnk.core.engine.http.HttpStatus;
import io.crnk.core.exception.BadRequestException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import dddrive.ddd.core.model.Aggregate;
import dddrive.ddd.core.model.AggregateRepository;
import dddrive.app.obj.model.Obj;
import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.AggregateDtoAdapterBase;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.AggregateDtoBase;
import io.zeitwert.dddrive.model.FMAggregateRepository;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class AggregateApiRepositoryBase<A extends Aggregate, D extends AggregateDtoBase<A>>
		extends ResourceRepositoryBase<D, Integer> {

	private final RequestContextFM requestCtx;
	private final ObjUserFMRepository userRepository;
	private final AggregateRepository<A> repository;
	private final AggregateDtoAdapterBase<A, D> dtoAdapter;

	public AggregateApiRepositoryBase(
			Class<D> dtoClass,
			RequestContext requestCtx,
			ObjUserFMRepository userRepository,
			AggregateRepository<A> repository,
			AggregateDtoAdapterBase<A, D> dtoAdapter) {
		super(dtoClass);
		this.requestCtx = (RequestContextFM) requestCtx;
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
			// this.userRepository.touch(this.requestCtx.getUser().getId());
			Integer tenantId = dto.getTenant() != null
					? Integer.parseInt(dto.getTenant().getId())
					: (Integer) this.requestCtx.getTenantId();
			A aggregate = this.repository.create(tenantId, requestCtx.getUserId(), requestCtx.getCurrentTime());
			this.dtoAdapter.toAggregate(dto, aggregate);
			this.repository.store(aggregate, requestCtx.getUserId(), requestCtx.getCurrentTime());
			return (S) this.dtoAdapter.fromAggregate(aggregate);
		} catch (Exception x) {
			throw new RuntimeException("crashed on create", x);
		}
	}

	@Override
	@Transactional
	public D findOne(Integer objId, QuerySpec querySpec) {
		try {
			// this.userRepository.touch(this.requestCtx.getUser().getId());
			A aggregate = this.repository.load(objId);
			this.requestCtx.addAggregate(objId, aggregate);
			return this.dtoAdapter.fromAggregate(aggregate);
		} catch (Exception x) {
			x.printStackTrace();
			throw new ResourceNotFoundException(this.repository.getAggregateType().getDefaultName() + "[" + objId + "]");
		}
	}

	@Override
	@Transactional
	public ResourceList<D> findAll(QuerySpec querySpec) {
		try {
			// this.userRepository.touch(this.requestCtx.getUser().getId());
			List<Object> itemList = ((FMAggregateRepository) this.repository).find(null);
			// List<A> itemList = this.repository.find(querySpec);
			ResourceList<D> list = new DefaultResourceList<>();
			list.addAll(itemList.stream().map(repository::get).map(this.dtoAdapter::fromAggregate).toList());
			return list;
		} catch (Exception x) {
			throw new RuntimeException("crashed on findAll", x);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public <S extends D> S save(S dto) {
		// this.userRepository.touch(this.requestCtx.getUser().getId());
		if (dto.getId() == null) {
			throw new BadRequestException("Can only save existing object (missing id)");
		} else if (dto.getMeta() == null) {
			throw new BadRequestException("Missing meta information (version or operation)");
		} else if (dto.getMeta().getClientVersion() == null
				&& !dto.getMeta().hasOperation(AggregateDtoBase.CalculationOnlyOperation)) {
			throw new BadRequestException("Missing meta information (version or operation)");
		}
		try {
			A aggregate = this.requestCtx.hasAggregate(dto.getId())
					? (A) this.requestCtx.getAggregate(dto.getId())
					: this.repository.load(dto.getId());
			if (dto.getMeta().hasOperation(AggregateDtoBase.CalculationOnlyOperation)) {
				this.dtoAdapter.toAggregate(dto, aggregate);
			} else {
				if (dto.getMeta().getClientVersion() == null) {
					throw new BadRequestException("Missing version");
				} else if (dto.getMeta().getClientVersion() != aggregate.getMeta().getVersion()) {
					ErrorData errorData = new ErrorDataBuilder()
							.setStatus("" + HttpStatus.CONFLICT_409)
							.setTitle("Fehler beim Speichern")
							.setDetail("Sie versuchten eine veraltete Version zu speichern."
									+ " Benutzer " + aggregate.getMeta().getModifiedByUser().getCaption()
									+ " hat das Objekt in der Zwischenzeit bereits geändert."
									+ " Ihre Änderungen wurden verworfen und die aktuelle Version geladen.")
							.build();
					throw new BadRequestException(HttpStatus.CONFLICT_409, errorData);
				}
				this.dtoAdapter.toAggregate(dto, aggregate);
				this.repository.store(aggregate, requestCtx.getUserId(), requestCtx.getCurrentTime());
				aggregate = this.repository.get(dto.getId());
			}
			return (S) this.dtoAdapter.fromAggregate(aggregate);
		} catch (Exception x) {
			throw new RuntimeException("crashed on save", x);
		}
	}

	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public void delete(Integer id) {
		// this.userRepository.touch(this.requestCtx.getUser().getId());
		if (id == null) {
			throw new ResourceNotFoundException("Can only delete existing object (missing id)");
		}
		try {
			A aggregate = this.requestCtx.hasAggregate(id)
					? (A) this.requestCtx.getAggregate(id)
					: this.repository.load(id);
			if (!(aggregate instanceof Obj)) {
				throw new BadRequestException("Can only delete an Object");
			}
			((Obj) aggregate).delete(requestCtx.getUserId(), requestCtx.getCurrentTime());
			this.repository.store(aggregate, requestCtx.getUserId(), requestCtx.getCurrentTime());
		} catch (Exception x) {
			throw new RuntimeException("crashed on delete", x);
		}
	}

}
