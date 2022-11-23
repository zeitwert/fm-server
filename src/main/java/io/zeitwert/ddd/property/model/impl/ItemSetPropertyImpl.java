package io.zeitwert.ddd.property.model.impl;

import io.zeitwert.ddd.property.model.ItemSetProperty;
import io.zeitwert.ddd.property.model.AggregatePartItem;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.zeitwert.ddd.util.Check.assertThis;

public class ItemSetPropertyImpl<Item extends AggregatePartItem<?>> extends PartListPropertyImpl<Item>
		implements ItemSetProperty<Item> {

	public ItemSetPropertyImpl(EntityWithPropertiesSPI entity, CodePartListType partListType) {
		super(entity, partListType);
	}

	@Override
	public Set<String> getItems() {
		return this.getPartList()
				.stream()
				.map(pi -> pi.getItemId())
				.collect(Collectors.toSet());
	}

	@Override
	public boolean hasItem(String itemId) {
		for (int i = 0; i < this.getPartCount(); i++) {
			Item part = this.getPart(i);
			if (part.getItemId().equals(itemId)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void clearItems() {
		this.clearPartList();
	}

	@Override
	public void addItem(String itemId) {
		if (itemId == null || itemId.length() == 0) {
			assertThis(false, "valid itemId");
		}
		if (!this.hasItem(itemId)) {
			AggregatePartItem<?> part = this.addPart();
			part.setItemId(itemId);
		}
	}

	@Override
	public void removeItem(String itemId) {
		if (itemId == null || itemId.length() == 0) {
			assertThis(false, "valid itemId");
		}
		Item part = this.getPartByItemId(itemId);
		if (part != null) {
			this.removePart(part.getId());
		}
	}

	private Item getPartByItemId(String itemId) {
		for (int i = 0; i < this.getPartCount(); i++) {
			Item part = this.getPart(i);
			if (part.getItemId().equals(itemId)) {
				return part;
			}
		}
		return null;
	}

	@Override
	public void loadItemSet(List<? extends Item> partList) {
		this.loadPartList(partList);
	}

}
