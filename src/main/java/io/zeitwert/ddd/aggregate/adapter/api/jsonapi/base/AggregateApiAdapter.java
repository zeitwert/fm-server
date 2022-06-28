
package io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base;

import java.util.List;

import org.jooq.TableRecord;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateDtoBase;
import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateDtoBridge;
import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.session.model.SessionInfo;

public abstract class AggregateApiAdapter<A extends Aggregate, V extends TableRecord<?>, D extends AggregateDtoBase<A>>
		extends ResourceRepositoryBase<D, Integer> {

	private final SessionInfo sessionInfo;

	private final AggregateRepository<A, V> repository;

	private final AggregateDtoBridge<A, V, D> bridge;

	public AggregateApiAdapter(Class<D> dtoClass, SessionInfo sessionInfo, AggregateRepository<A, V> repository,
			AggregateDtoBridge<A, V, D> bridge) {
		super(dtoClass);
		this.sessionInfo = sessionInfo;
		this.repository = repository;
		this.bridge = bridge;
	}

	@Override
	@SuppressWarnings("unchecked")
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
	public D findOne(Integer objId, QuerySpec querySpec) {
		A aggregate = this.repository.get(this.sessionInfo, objId);
		return this.bridge.fromAggregate(aggregate, this.sessionInfo);
	}

	@Override
	public ResourceList<D> findAll(QuerySpec querySpec) {
		List<V> itemList = this.repository.find(this.sessionInfo, querySpec);
		ResourceList<D> list = new DefaultResourceList<>();
		list.addAll(itemList.stream().map(item -> this.bridge.fromRecord(item, this.sessionInfo)).toList());
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <S extends D> S save(S dto) {
		if (dto.getId() == null) {
			throw new BadRequestException("Can only save existing object (missing id)");
		}
		A aggregate = this.repository.get(this.sessionInfo, dto.getId());
		if (dto.getMeta().hasOperation(AggregateDtoBase.DiscardOperation)) {
			this.repository.discard(aggregate);
		} else {
			this.bridge.toAggregate(dto, aggregate);
			if (!dto.getMeta().hasOperation(AggregateDtoBase.CalculationOnlyOperation)) {
				this.repository.store(aggregate);
			}
		}
		aggregate = this.repository.get(this.sessionInfo, dto.getId());
		return (S) this.bridge.fromAggregate(aggregate, this.sessionInfo);
	}

	@Override
	public void delete(Integer id) {
		if (id == null) {
			throw new BadRequestException("Can only delete existing object (missing id)");
		}
		A aggregate = this.repository.get(this.sessionInfo, id);
		if (!(aggregate instanceof Obj)) {
			throw new BadRequestException("Can only delete an Object");
		}
		((Obj) aggregate).delete();
		this.repository.store(aggregate);
	}

}
