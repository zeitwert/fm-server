
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
import io.zeitwert.ddd.session.model.SessionInfo;

public abstract class AggregateApiRepositoryBase<A extends Aggregate, V extends TableRecord<?>, D extends AggregateDtoBase<A>>
		extends ResourceRepositoryBase<D, Integer> {

	private final SessionInfo sessionInfo;
	private final AggregateRepository<A, V> repository;
	private final AggregateDtoAdapter<A, V, D> bridge;

	public AggregateApiRepositoryBase(Class<D> dtoClass, SessionInfo sessionInfo, AggregateRepository<A, V> repository,
			AggregateDtoAdapter<A, V, D> bridge) {
		super(dtoClass);
		this.sessionInfo = sessionInfo;
		this.repository = repository;
		this.bridge = bridge;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public <S extends D> S create(S dto) {
		if (dto.getId() != null) {
			throw new BadRequestException("Cannot specify id on creation (" + dto.getId() + ")");
		}
		A aggregate = this.repository.create(this.sessionInfo);
		this.bridge.toAggregate(dto, aggregate);
		this.repository.store(aggregate);
		return (S) this.bridge.fromAggregate(aggregate, this.sessionInfo);
	}

	@Override
	@Transactional
	public D findOne(Integer objId, QuerySpec querySpec) {
		try {
			A aggregate = this.repository.get(this.sessionInfo, objId);
			this.sessionInfo.addAggregate(aggregate);
			return this.bridge.fromAggregate(aggregate, this.sessionInfo);
		} catch (NoDataFoundException x) {
			throw new ResourceNotFoundException(repository.getAggregateType().getName() + "[" + objId + "]");
		}
	}

	@Override
	@Transactional
	public ResourceList<D> findAll(QuerySpec querySpec) {
		List<V> itemList = this.repository.find(this.sessionInfo, querySpec);
		ResourceList<D> list = new DefaultResourceList<>();
		list.addAll(itemList.stream().map(item -> this.bridge.fromRecord(item, this.sessionInfo)).toList());
		return list;
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
		A aggregate = this.sessionInfo.hasAggregate(dto.getId())
				? (A) this.sessionInfo.getAggregate(dto.getId())
				: this.repository.get(this.sessionInfo, dto.getId());
		if (dto.getMeta().hasOperation(AggregateDtoBase.DiscardOperation)) {
			this.repository.discard(aggregate);
		} else if (dto.getMeta().hasOperation(AggregateDtoBase.CalculationOnlyOperation)) {
			this.bridge.toAggregate(dto, aggregate);
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
			this.bridge.toAggregate(dto, aggregate);
			this.repository.store(aggregate);
			aggregate = this.repository.get(this.sessionInfo, dto.getId());
		}
		return (S) this.bridge.fromAggregate(aggregate, this.sessionInfo);
	}

	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public void delete(Integer id) {
		if (id == null) {
			throw new BadRequestException("Can only delete existing object (missing id)");
		}
		A aggregate = this.sessionInfo.hasAggregate(id)
				? (A) this.sessionInfo.getAggregate(id)
				: this.repository.get(this.sessionInfo, id);
		if (!(aggregate instanceof Obj)) {
			throw new BadRequestException("Can only delete an Object");
		}
		((Obj) aggregate).delete();
		this.repository.store(aggregate);
	}

}
