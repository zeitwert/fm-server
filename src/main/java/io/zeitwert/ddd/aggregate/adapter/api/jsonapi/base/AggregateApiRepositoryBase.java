
package io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base;

import java.util.List;

import org.jooq.TableRecord;
import org.jooq.exception.NoDataFoundException;
import org.springframework.transaction.annotation.Transactional;

import io.crnk.core.engine.document.ErrorData;
import io.crnk.core.engine.document.ErrorDataBuilder;
import io.crnk.core.engine.http.HttpStatus;
import io.crnk.core.exception.BadRequestException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateDtoBase;
import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateDtoAdapter;
import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.session.model.RequestContext;

public abstract class AggregateApiRepositoryBase<A extends Aggregate, V extends TableRecord<?>, D extends AggregateDtoBase<A>>
		extends ResourceRepositoryBase<D, Integer> {

	private final RequestContext requestCtx;
	private final AggregateRepository<A, V> repository;
	private final AggregateDtoAdapter<A, V, D> dtoAdapter;

	public AggregateApiRepositoryBase(Class<D> dtoClass, RequestContext requestCtx, AggregateRepository<A, V> repository,
			AggregateDtoAdapter<A, V, D> dtoAdapter) {
		super(dtoClass);
		this.requestCtx = requestCtx;
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
			Integer tenantId = dto.getTenant() != null
					? Integer.parseInt(dto.getTenant().getId())
					: requestCtx.getTenant().getId();
			A aggregate = this.repository.create(tenantId, this.requestCtx);
			this.dtoAdapter.toAggregate(dto, aggregate, this.requestCtx);
			this.repository.store(aggregate);
			return (S) this.dtoAdapter.fromAggregate(aggregate, this.requestCtx);
		} catch (Exception x) {
			throw new RuntimeException("crashed on create", x);
		}
	}

	@Override
	@Transactional
	public D findOne(Integer objId, QuerySpec querySpec) {
		try {
			A aggregate = this.repository.get(this.requestCtx, objId);
			this.requestCtx.addAggregate(aggregate);
			return this.dtoAdapter.fromAggregate(aggregate, this.requestCtx);
		} catch (NoDataFoundException x) {
			throw new ResourceNotFoundException(repository.getAggregateType().getName() + "[" + objId + "]");
		}
	}

	@Override
	@Transactional
	public ResourceList<D> findAll(QuerySpec querySpec) {
		try {
			List<V> itemList = this.repository.find(this.requestCtx, querySpec);
			ResourceList<D> list = new DefaultResourceList<>();
			list.addAll(itemList.stream().map(item -> this.dtoAdapter.fromRecord(item, this.requestCtx)).toList());
			return list;
		} catch (Exception x) {
			throw new RuntimeException("crashed on findAll", x);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public <S extends D> S save(S dto) {
		if (dto.getId() == null) {
			throw new BadRequestException("Can only save existing object (missing id)");
		} else if (dto.getMeta() == null) {
			throw new BadRequestException("Missing meta information (version or operation)");
		} else if (dto.getMeta().getClientVersion() == null
				&& !dto.getMeta().hasOperation(AggregateDtoBase.DiscardOperation)
				&& !dto.getMeta().hasOperation(AggregateDtoBase.CalculationOnlyOperation)) {
			throw new BadRequestException("Missing meta information (version or operation)");
		}
		try {
			A aggregate = this.requestCtx.hasAggregate(dto.getId())
					? (A) this.requestCtx.getAggregate(dto.getId())
					: this.repository.get(this.requestCtx, dto.getId());
			if (dto.getMeta().hasOperation(AggregateDtoBase.DiscardOperation)) {
				this.repository.discard(aggregate);
			} else if (dto.getMeta().hasOperation(AggregateDtoBase.CalculationOnlyOperation)) {
				this.dtoAdapter.toAggregate(dto, aggregate, this.requestCtx);
			} else {
				if (dto.getMeta().getClientVersion() == null) {
					throw new BadRequestException("Missing version");
				} else if (dto.getMeta().getClientVersion().intValue() != aggregate.getMeta().getVersion().intValue()) {
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
				this.dtoAdapter.toAggregate(dto, aggregate, this.requestCtx);
				this.repository.store(aggregate);
				aggregate = this.repository.get(this.requestCtx, dto.getId());
			}
			return (S) this.dtoAdapter.fromAggregate(aggregate, this.requestCtx);
		} catch (Exception x) {
			throw new RuntimeException("crashed on save", x);
		}
	}

	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public void delete(Integer id) {
		if (id == null) {
			throw new ResourceNotFoundException("Can only delete existing object (missing id)");
		}
		try {
			A aggregate = this.requestCtx.hasAggregate(id)
					? (A) this.requestCtx.getAggregate(id)
					: this.repository.get(this.requestCtx, id);
			if (!(aggregate instanceof Obj)) {
				throw new BadRequestException("Can only delete an Object");
			}
			((Obj) aggregate).delete();
			this.repository.store(aggregate);
		} catch (Exception x) {
			throw new RuntimeException("crashed on delete", x);
		}
	}

}
