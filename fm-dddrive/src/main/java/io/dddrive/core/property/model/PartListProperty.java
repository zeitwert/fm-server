package io.dddrive.core.property.model;

import java.util.List;

import io.dddrive.core.ddd.model.Part;

public interface PartListProperty<P extends Part<?>> extends Property<P> {

	Class<P> getPartType();

	Integer getPartCount();

	P getPart(Integer seqNr);

	P getPartById(Integer partId);

	List<P> getParts();

	void clearParts();

	P addPart(Integer partId);

	void removePart(Integer partId);

	void removePart(P part);

	/**
	 * Returns the index of the specified part within this list.
	 *
	 * @param part the part to find
	 * @return the index of the part, or -1 if the part is not in this list.
	 */
	int getIndexOfPart(Part<?> part);

}
