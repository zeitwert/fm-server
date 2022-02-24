package io.zeitwert.ddd.property.model.impl;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.part.model.base.PartSPI;
import io.zeitwert.ddd.property.model.EntityPartItem;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.ddd.property.model.base.PropertyBase;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.Assert;

public class ReferenceSetPropertyImpl<A extends Aggregate> extends PropertyBase<A> implements ReferenceSetProperty<A> {

	private final CodePartListType partListType;

	private Set<EntityPartItem> itemSet = new HashSet<>();

	// repository is not used, since checking it is too expensive (for now)
	public ReferenceSetPropertyImpl(EntityWithPropertiesSPI entity, CodePartListType partListType,
			AggregateRepository<A, ?> repository) {
		super(entity);
		this.partListType = partListType;
	}

	@Override
	public String getName() {
		return this.partListType.getId();
	}

	@Override
	public CodePartListType getPartListType() {
		return this.partListType;
	}

	@Override
	public Set<Integer> getItems() {
		return Set.copyOf(this.itemSet.stream().map(item -> Integer.valueOf(item.getItemId())).toList());
	}

	@Override
	public boolean hasItem(Integer aggregateId) {
		for (EntityPartItem part : this.itemSet) {
			if (part.getItemId().equals(aggregateId.toString())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void clearItems() {
		this.itemSet.forEach(item -> ((PartSPI<?>) item).delete());
		this.itemSet.clear();
	}

	@Override
	public void addItem(Integer aggregateId) {
		Assert.isTrue(aggregateId != null, "aggregateId not null");
		if (!this.hasItem(aggregateId)) {
			EntityPartItem part = (EntityPartItem) this.getEntity().addItem(this, this.partListType);
			part.setItemId(aggregateId.toString());
			this.itemSet.add(part);
			this.getEntity().afterAdd(this);
		}
	}

	@Override
	public void removeItem(Integer aggregateId) {
		Assert.isTrue(aggregateId != null, "aggregateId not null");
		if (this.hasItem(aggregateId)) {
			EntityPartItem part = this.itemSet.stream().filter(p -> p.getItemId().equals(aggregateId.toString())).findAny()
					.get();
			((PartSPI<?>) part).delete();
			this.itemSet.remove(part);
			this.getEntity().afterRemove(this);
		}
	}

	@Override
	public void beforeStore() {
		int seqNr = 0;
		for (EntityPartItem item : this.itemSet) {
			item.setSeqNr(seqNr++);
		}
	}

	@Override
	public void loadReferenceSet(Collection<EntityPartItem> partList) {
		this.itemSet.clear();
		partList.forEach(p -> this.itemSet.add(p));
	}

}
