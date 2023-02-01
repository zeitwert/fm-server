package io.zeitwert.ddd.property.model.impl;

import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;
import io.zeitwert.ddd.part.model.base.PartSPI;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.AggregatePartItem;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.ddd.property.model.base.PropertyBase;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static io.zeitwert.ddd.util.Check.assertThis;

public class EnumSetPropertyImpl<E extends Enumerated> extends PropertyBase<E> implements EnumSetProperty<E> {

	private final String name;
	private final CodePartListType partListType;
	private final Enumeration<E> enumeration;

	private Set<AggregatePartItem<?>> itemSet = new HashSet<>();

	public EnumSetPropertyImpl(EntityWithPropertiesSPI entity, CodePartListType partListType,
			Enumeration<E> enumeration) {
		super(entity);
		this.name = partListType.getId();
		this.partListType = partListType;
		this.enumeration = enumeration;
	}

	public EnumSetPropertyImpl(EntityWithPropertiesSPI entity, String name, CodePartListType partListType,
			Enumeration<E> enumeration) {
		super(entity);
		this.name = name;
		this.partListType = partListType;
		this.enumeration = enumeration;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public CodePartListType getPartListType() {
		return this.partListType;
	}

	@Override
	public void clearItems() {
		this.itemSet.forEach(item -> ((PartSPI<?>) item).delete());
		this.itemSet.clear();
		this.getEntity().afterClear(this);
	}

	@Override
	public void addItem(E item) {
		assertThis(item != null, "item not null");
		if (item == null) {
			return; // make compiler happy (potential null pointer)
		}
		if (!this.hasItem(item)) {
			AggregatePartItem<?> part = (AggregatePartItem<?>) this.getEntity().addPart(this, this.partListType);
			assertThis(part != null,
					"entity " + this.getEntity().getClass().getSimpleName() + "created a part for " + this.partListType.getId()
							+ " (make sure to compare property with .equals() in addPart)");
			if (part == null) {
				return; // make compiler happy (potential null pointer)
			}
			part.setItemId(item.getId());
			this.itemSet.add(part);
			this.getEntity().afterAdd(this);
		}
	}

	@Override
	public Set<E> getItems() {
		return Set.copyOf(this.itemSet.stream().map(item -> this.enumeration.getItem(item.getItemId())).toList());
	}

	@Override
	public boolean hasItem(E item) {
		for (AggregatePartItem<?> part : this.itemSet) {
			if (part.getItemId().equals(item.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void removeItem(E item) {
		assertThis(item != null, "item not null");
		if (item == null) {
			return; // make compiler happy (potential null pointer)
		}
		if (this.hasItem(item)) {
			AggregatePartItem<?> part = this.itemSet.stream().filter(p -> p.getItemId().equals(item.getId())).findAny().get();
			((PartSPI<?>) part).delete();
			this.itemSet.remove(part);
			this.getEntity().afterRemove(this);
		}
	}

	public void doBeforeStore() {
		int seqNr = 0;
		for (AggregatePartItem<?> item : this.itemSet) {
			item.setSeqNr(seqNr++);
		}
	}

	@Override
	public void loadEnums(Collection<? extends AggregatePartItem<?>> partList) {
		this.itemSet.clear();
		partList.forEach(p -> this.itemSet.add(p));
	}

}
