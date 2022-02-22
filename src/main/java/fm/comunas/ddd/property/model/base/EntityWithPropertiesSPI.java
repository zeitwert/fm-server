package fm.comunas.ddd.property.model.base;

import fm.comunas.ddd.entity.model.EntityMeta;
import fm.comunas.ddd.part.model.Part;
import fm.comunas.ddd.property.model.EntityPartItem;
import fm.comunas.ddd.property.model.Property;
import fm.comunas.ddd.property.model.enums.CodePartListType;

public interface EntityWithPropertiesSPI {

	EntityMeta getMeta();

	<P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType);

	EntityPartItem addItem(Property<?> property, CodePartListType partListType);

	void afterSet(Property<?> property);

	void afterAdd(Property<?> property);

	void afterRemove(Property<?> property);

}
