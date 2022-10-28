package io.zeitwert.ddd.property.model.impl;

import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.base.PartBase;
import io.zeitwert.ddd.part.model.base.PartSPI;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.ddd.property.model.base.PropertyBase;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.zeitwert.ddd.util.Check.assertThis;

public class PartListPropertyImpl<P extends Part<?>> extends PropertyBase<P> implements PartListProperty<P> {

	private final CodePartListType partListType;

	private List<P> partList = new ArrayList<>();

	public PartListPropertyImpl(EntityWithPropertiesSPI entity, CodePartListType partListType) {
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

	public void clearPartList() {
		for (P part : this.partList) {
			((PartSPI<?>) part).delete();
		}
		this.partList.clear();
		this.getEntity().afterClear(this);
	}

	public P addPart() {
		P part = this.getEntity().addPart(this, this.partListType);
		this.partList.add(part);
		this.getEntity().afterAdd(this);
		return part;
	}

	public Integer getPartCount() {
		return this.partList.size();
	}

	public P getPart(Integer seqNr) {
		assertThis(0 <= seqNr && seqNr < this.getPartCount(), "valid seqNr (" + seqNr + ")");
		return this.partList.get(seqNr);
	}

	public P getPartById(Integer partId) {
		assertThis(partId != null, "valid partId");
		if (partId == null) {
			return null; // make compiler happy (potential null pointer)
		}
		Optional<P> part = this.partList.stream().filter(p -> partId.equals(p.getId())).findAny();
		assertThis(part.isPresent(), "part with id " + partId + " must exist");
		return part.get();
	}

	public List<P> getPartList() {
		return List.copyOf(this.partList);
	}

	public void removePart(Integer partId) {
		P part = this.getPartById(partId);
		((PartSPI<?>) part).delete();
		this.partList.remove(part);
		this.getEntity().afterRemove(this);
	}

	@SuppressWarnings("unchecked")
	public void loadPartList(List<? extends Part<?>> partList) {
		this.partList.clear();
		partList.forEach(p -> this.partList.add((P) p));
	}

	public void doBeforeStore() {
		int seqNr = 0;
		for (P part : this.partList) {
			((PartBase<?>) part).setSeqNr(seqNr++);
		}
	}

}
