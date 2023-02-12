package io.dddrive.property.model.impl;

import io.dddrive.ddd.model.Part;
import io.dddrive.ddd.model.base.PartBase;
import io.dddrive.ddd.model.base.PartSPI;
import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.property.model.PartListProperty;
import io.dddrive.property.model.base.EntityWithPropertiesSPI;
import io.dddrive.property.model.base.PropertyBase;

import static io.dddrive.util.Invariant.assertThis;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PartListPropertyImpl<P extends Part<?>> extends PropertyBase<P> implements PartListProperty<P> {

	private final String name;
	private final Class<P> partType;
	private final CodePartListType partListType;
	private final List<P> partList = new ArrayList<>();

	public PartListPropertyImpl(EntityWithPropertiesSPI entity, String name, CodePartListType partListType,
			Class<P> partType) {
		super(entity);
		this.name = name;
		this.partType = partType;
		this.partListType = partListType;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Class<P> getPartType() {
		return this.partType;
	}

	@Override
	public CodePartListType getPartListType() {
		return this.partListType;
	}

	@Override
	public void clearParts() {
		for (P part : this.partList) {
			((PartSPI<?>) part).delete();
		}
		this.partList.clear();
		this.getEntity().afterClear(this);
	}

	@Override
	@SuppressWarnings("unchecked")
	public P addPart() {
		P part = (P) this.getEntity().addPart(this, this.partListType);
		assertThis(part != null,
				"entity " + this.getEntity().getClass().getSimpleName() + "created a part for " + this.partListType.getId()
						+ " (make sure to compare property with .equals() in addPart)");
		this.partList.add(part);
		this.getEntity().afterAdd(this);
		return part;
	}

	@Override
	public Integer getPartCount() {
		return this.partList.size();
	}

	@Override
	public P getPart(Integer seqNr) {
		assertThis(0 <= seqNr && seqNr < this.getPartCount(), "valid seqNr (" + seqNr + ")");
		return this.partList.get(seqNr);
	}

	@Override
	public P getPartById(Integer partId) {
		assertThis(partId != null, "valid partId");
		if (partId == null) {
			return null; // make compiler happy (potential null pointer)
		}
		Optional<P> part = this.partList.stream().filter(p -> partId.equals(p.getId())).findAny();
		assertThis(part.isPresent(), "part with id " + partId + " must exist");
		return part.get();
	}

	@Override
	public List<P> getParts() {
		return List.copyOf(this.partList);
	}

	@Override
	public void removePart(Integer partId) {
		P part = this.getPartById(partId);
		((PartSPI<?>) part).delete();
		this.partList.remove(part);
		this.getEntity().afterRemove(this);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void loadParts(List<? extends Part<?>> partList) {
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
