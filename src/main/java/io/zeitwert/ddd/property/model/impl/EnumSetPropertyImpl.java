package io.zeitwert.ddd.property.model.impl;

import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;
import io.zeitwert.ddd.part.model.base.PartSPI;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.EntityPartItem;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.ddd.property.model.base.PropertyBase;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.Assert;

public class EnumSetPropertyImpl<E extends Enumerated> extends PropertyBase<E> implements EnumSetProperty<E> {

	private final CodePartListType partListType;
	private final Enumeration<E> enumeration;

	private Set<EntityPartItem> itemSet = new HashSet<>();

	public EnumSetPropertyImpl(EntityWithPropertiesSPI entity, CodePartListType partListType,
			Enumeration<E> enumeration) {
		super(entity);
		this.partListType = partListType;
		this.enumeration = enumeration;
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
	public Set<E> getItems() {
		return Set.copyOf(this.itemSet.stream().map(item -> this.enumeration.getItem(item.getItemId())).toList());
	}

	@Override
	public boolean hasItem(E item) {
		for (EntityPartItem part : this.itemSet) {
			if (part.getItemId().equals(item.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void clearItems() {
		this.itemSet.forEach(item -> ((PartSPI<?>) item).delete());
		this.itemSet.clear();
		this.getEntity().afterClear(this);
	}

	@Override
	public void addItem(E item) {
		Assert.isTrue(item != null, "item not null");
		if (!this.hasItem(item)) {
			EntityPartItem part = this.getEntity().addItem(this, this.partListType);
			part.setItemId(item.getId());
			this.itemSet.add(part);
			this.getEntity().afterAdd(this);
		}
	}

	@Override
	public void removeItem(E item) {
		Assert.isTrue(item != null, "item not null");
		if (this.hasItem(item)) {
			EntityPartItem part = this.itemSet.stream().filter(p -> p.getItemId().equals(item.getId())).findAny().get();
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
	public void loadEnumSet(Collection<EntityPartItem> partList) {
		this.itemSet.clear();
		partList.forEach(p -> this.itemSet.add(p));
	}

}
