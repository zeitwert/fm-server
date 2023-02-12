package io.dddrive.property.model;

import java.util.List;

import io.dddrive.ddd.model.Part;

public interface PartListProperty<P extends Part<?>> extends CollectionProperty<P> {

	Class<P> getPartType();

	Integer getPartCount();

	P getPart(Integer seqNr);

	P getPartById(Integer partId);

	List<P> getParts();

	void clearParts();

	P addPart();

	void removePart(Integer partId);

	void loadParts(List<? extends Part<?>> partList);

}
