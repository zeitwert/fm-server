package fm.comunas.ddd.property.model;

import fm.comunas.ddd.part.model.Part;

import java.util.Collection;
import java.util.List;

public interface PartListProperty<P extends Part<?>> extends CollectionProperty<P> {

	Integer getPartCount();

	P getPart(Integer seqNr);

	P getPartById(Integer partId);

	List<P> getPartList();

	void clearPartList();

	P addPart();

	void removePart(Integer partId);

	void loadPartList(Collection<Part<?>> partList);

}
