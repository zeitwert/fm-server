package io.dddrive.core.property.model;

import org.springframework.lang.Nullable;

import io.dddrive.core.ddd.model.Part;
import io.dddrive.core.ddd.model.RepositoryDirectory;

public interface EntityWithPropertiesSPI {

	RepositoryDirectory getDirectory();

	boolean isInLoad();

	boolean isInCalc();

	Property<?> getParentProperty();

	String getRelativePath();

	String getPath();

	void fireEntityAddedChange(Object id);

	void fireEntityRemovedChange();

	void fireValueAddedChange(Property<?> property, Object value);

	void fireValueRemovedChange(Property<?> property, Object value);

	void fireFieldSetChange(Property<?> property, Object value, Object oldValue);

	void fireFieldChange(String op, String path, String value, String oldValue, boolean isInCalc);

	Part<?> doAddPart(Property<?> property, Integer partId);

	void doBeforeSet(Property<?> property, @Nullable Object value, @Nullable Object oldValue);

	void doAfterSet(Property<?> property);

	void doAfterClear(Property<?> property);

	void doAfterAdd(Property<?> property, Part<?> part);

	void doAfterRemove(Property<?> property);

}
