package io.zeitwert.ddd.property.model;

import io.zeitwert.ddd.part.model.Part;

import java.util.List;

public interface PartListProperty<P extends Part<?>> extends CollectionProperty<P> {

	Integer getPartCount();

	P getPart(Integer seqNr);

	P getPartById(Integer partId);

	List<P> getPartList();

	void clearPartList();

	P addPart();

	void removePart(Integer partId);

	void loadPartList(List<? extends Part<?>> partList);

}
