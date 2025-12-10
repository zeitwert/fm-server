package io.dddrive.core.property.model.impl;

import static io.dddrive.util.Invariant.assertThis;
import static io.dddrive.util.Invariant.requireThis;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.dddrive.core.ddd.model.Part;
import io.dddrive.core.ddd.model.PartSPI;
import io.dddrive.core.property.model.EntityWithProperties;
import io.dddrive.core.property.model.EntityWithPropertiesSPI;
import io.dddrive.core.property.model.PartListProperty;
import io.dddrive.core.property.model.base.PropertyBase;

public class PartListPropertyImpl<P extends Part<?>> extends PropertyBase<P> implements PartListProperty<P> {

	private final Class<P> partType;
	private final List<P> partList = new ArrayList<>();

	public PartListPropertyImpl(EntityWithProperties entity, String name, Class<P> partType) {
		super(entity, name);
		this.partType = partType;
	}

	@Override
	public Class<P> getPartType() {
		return this.partType;
	}

	@Override
	public void clearParts() {
		requireThis(this.isWritable(), "not frozen");
		for (P part : this.partList) {
			((PartSPI<?>) part).delete();
		}
		this.partList.clear();
		((EntityWithPropertiesSPI) this.getEntity()).doAfterClear(this);
	}

	@Override
	@SuppressWarnings("unchecked")
	public P addPart(Integer partId) {
		requireThis(this.isWritable(), "not frozen");
		EntityWithPropertiesSPI entity = (EntityWithPropertiesSPI) this.getEntity();
		P part = (P) entity.doAddPart(this, partId);
		assertThis(part != null, "entity " + this.getEntity().getClass().getSimpleName() + "created a part for " + this.getName());
		this.partList.add(part);
		((EntityWithPropertiesSPI) part).fireEntityAddedChange(part.getId());
		entity.doAfterAdd(this, part);
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
		requireThis(this.isWritable(), "not frozen");
		this.removePart(this.getPartById(partId));
	}

	@Override
	public void removePart(P part) {
		requireThis(this.isWritable(), "not frozen");
		((EntityWithPropertiesSPI) part).fireEntityRemovedChange();
		((PartSPI<?>) part).delete();
		this.partList.remove(part);
		((EntityWithPropertiesSPI) this.getEntity()).doAfterRemove(this);
	}

	@Override
	public int getIndexOfPart(Part<?> part) {
		return this.partList.indexOf(part);
	}

}
