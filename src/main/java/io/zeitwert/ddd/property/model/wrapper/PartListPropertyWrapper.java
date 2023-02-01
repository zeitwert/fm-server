package io.zeitwert.ddd.property.model.wrapper;

import java.util.List;

import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;

public class PartListPropertyWrapper<P extends Part<?>> extends PropertyWrapperBase<P> implements PartListProperty<P> {

	private PartListProperty<P> property;

	public PartListPropertyWrapper(EntityWithPropertiesSPI entity, String name, Class<P> type) {
		super(entity, name, type);
	}

	@Override
	public PartListProperty<P> getProperty() {
		if (this.property == null) {
			this.property = this.entity.getPropertyProvider().getPartListProperty(this.entity, this.name, this.type);
		}
		return this.property;
	}

	@Override
	public CodePartListType getPartListType() {
		return this.getProperty().getPartListType();
	}

	@Override
	public Integer getPartCount() {
		return this.getProperty().getPartCount();
	}

	@Override
	public P getPart(Integer seqNr) {
		return this.getProperty().getPart(seqNr);
	}

	@Override
	public P getPartById(Integer partId) {
		return this.getProperty().getPartById(partId);
	}

	@Override
	public List<P> getParts() {
		return this.getProperty().getParts();
	}

	@Override
	public void clearParts() {
		this.getProperty().clearParts();
	}

	@Override
	public P addPart() {
		return this.getProperty().addPart();
	}

	@Override
	public void removePart(Integer partId) {
		this.getProperty().removePart(partId);
	}

	@Override
	public void loadParts(List<? extends Part<?>> partList) {
		this.getProperty().loadParts(partList);
	}

}
