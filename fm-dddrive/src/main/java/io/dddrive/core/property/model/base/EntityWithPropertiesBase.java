package io.dddrive.core.property.model.base;

import static io.dddrive.util.Invariant.assertThis;
import static io.dddrive.util.Invariant.requireThis;
import static java.lang.Integer.max;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import io.dddrive.core.ddd.model.Aggregate;
import io.dddrive.core.ddd.model.AggregateRepository;
import io.dddrive.core.ddd.model.Part;
import io.dddrive.core.ddd.model.PartRepository;
import io.dddrive.core.enums.model.Enumerated;
import io.dddrive.core.enums.model.Enumeration;
import io.dddrive.core.property.model.*;
import io.dddrive.core.property.model.impl.BasePropertyImpl;
import io.dddrive.core.property.model.impl.EnumPropertyImpl;
import io.dddrive.core.property.model.impl.EnumSetPropertyImpl;
import io.dddrive.core.property.model.impl.PartListPropertyImpl;
import io.dddrive.core.property.model.impl.PartReferencePropertyImpl;
import io.dddrive.core.property.model.impl.ReferencePropertyImpl;
import io.dddrive.core.property.model.impl.ReferenceSetPropertyImpl;

public abstract class EntityWithPropertiesBase implements EntityWithProperties, EntityWithPropertiesSPI {

	private static final Pattern SEGMENT_PATTERN = Pattern.compile("(\\w+)(?:\\[(\\d+)])?");

	private final Map<String, Property<?>> propertyMap = new HashMap<>();
	private final Map<Integer, Part<?>> partMap = new HashMap<>();

	@Override
	public boolean hasProperty(String name) {
		return this.propertyMap.containsKey(name);
	}

	@Override
	public Property<?> getProperty(String name) {
		return this.propertyMap.get(name);
	}

	@Override
	public List<Property<?>> getProperties() {
		return this.propertyMap.values().stream().toList();
	}

	@Override
	public boolean hasPart(Integer partId) {
		return this.partMap.containsKey(partId);
	}

	@Override
	public Part<?> getPart(Integer partId) {
		return this.partMap.get(partId);
	}

	protected void addPart(Part<?> part) {
		this.partMap.put(part.getId(), part);
	}

	protected void addProperty(Property<?> property) {
		requireThis(property.getName() != null, "property has name");
		requireThis(!this.hasProperty(property.getName()), "property [" + property.getName() + "] is unique");
		this.propertyMap.put(property.getName(), property);
	}

	protected <T> BaseProperty<T> addBaseProperty(String name, Class<T> type) {
		BaseProperty<T> property = new BasePropertyImpl<>(this, name, type);
		this.addProperty(property);
		return property;
	}

	protected <E extends Enumerated> EnumProperty<E> addEnumProperty(String name, Class<E> enumType) {
		Enumeration<E> enumeration = this.getDirectory().getEnumeration(enumType);
		assertThis(enumeration != null, "enumeration for " + enumType.getSimpleName() + " not null");
		EnumProperty<E> property = new EnumPropertyImpl<>(this, name, enumeration, enumType);
		this.addProperty(property);
		return property;
	}

	protected <E extends Enumerated> EnumSetProperty<E> addEnumSetProperty(String name, Class<E> enumType) {
		Enumeration<E> enumeration = this.getDirectory().getEnumeration(enumType);
		assertThis(enumeration != null, "enumeration for " + enumType.getSimpleName() + " not null");
		EnumSetProperty<E> property = new EnumSetPropertyImpl<>(this, name, enumeration);
		this.addProperty(property);
		return property;
	}

	protected <A extends Aggregate> ReferenceProperty<A> addReferenceProperty(String name, Class<A> aggregateType) {
		AggregateRepository<A> repo = this.getDirectory().getRepository(aggregateType);
		assertThis(repo != null, () -> "repo for " + aggregateType.getSimpleName() + " not null");
		ReferenceProperty<A> property = new ReferencePropertyImpl<>(this, name, repo::get, aggregateType);
		this.addProperty(property);
		return property;
	}

	protected <A extends Aggregate> ReferenceSetProperty<A> addReferenceSetProperty(String name, Class<A> aggregateType) {
		AggregateRepository<A> repo = this.getDirectory().getRepository(aggregateType);
		assertThis(repo != null, () -> "repo for " + aggregateType.getSimpleName() + " not null");
		ReferenceSetProperty<A> property = new ReferenceSetPropertyImpl<>(this, name, repo::get);
		this.addProperty(property);
		return property;
	}

	protected <P extends Part<?>> PartListProperty<P> addPartListProperty(String name, Class<P> partType) {
		PartListProperty<P> property = new PartListPropertyImpl<>(this, name, partType);
		this.addProperty(property);
		return property;
	}

	@SuppressWarnings("unchecked")
	protected <A extends Aggregate, P extends Part<A>> PartReferenceProperty<P> addPartReferenceProperty(String name, Class<P> partType) {
		PartRepository<A, P> repo = this.getDirectory().getPartRepository(partType);
		assertThis(repo != null, () -> "repo for " + partType.getSimpleName() + " not null");
		PartReferenceProperty<P> property = new PartReferencePropertyImpl<P>(this, name, id -> (P) this.getPart(id), partType);
		this.addProperty(property);
		return property;
	}

	@Override
	public Property<?> getParentProperty() {
		return null;
	}

	@Override
	public void fireEntityAddedChange(Object id) {
		if (!this.isInLoad() && this.doLogChange(this)) {
			String path = this.getPath();
			int partEndIndex = path.lastIndexOf(".");
			int aggregateEndIndex = path.lastIndexOf("(");
			path = path.substring(0, max(partEndIndex, aggregateEndIndex));
			this.fireFieldChange("add", path, id.toString(), null, this.isInCalc());
		}
	}

	@Override
	public void fireEntityRemovedChange() {
		if (!this.isInLoad() && this.doLogChange(this)) {
			this.fireFieldChange("remove", this.getPath(), null, null, this.isInCalc());
		}
	}

	@Override
	public void fireValueAddedChange(Property<?> property, Object value) {
		if (!this.isInLoad() && this.doLogChange(this)) {
			this.fireFieldChange("add", property.getPath(), value.toString(), null, this.isInCalc());
		}
	}

	@Override
	public void fireValueRemovedChange(Property<?> property, Object value) {
		if (!this.isInLoad() && this.doLogChange(this)) {
			this.fireFieldChange("remove", property.getPath(), value.toString(), null, this.isInCalc());
		}
	}

	@Override
	public void fireFieldSetChange(Property<?> property, Object value, Object oldValue) {
		if (!this.isInLoad() && this.doLogChange(property)) {
			if (oldValue == null) {
				this.fireFieldChange("add", property.getPath(), value != null ? value.toString() : null, null, this.isInCalc());
			} else {
				this.fireFieldChange("replace", property.getPath(), value != null ? value.toString() : null, oldValue.toString(), this.isInCalc());
			}
		}
	}

	protected boolean doLogChange(EntityWithProperties entity) {
		Property<?> parentProperty = ((EntityWithPropertiesSPI) entity).getParentProperty();
		return parentProperty == null || doLogChange(parentProperty);
	}

	protected boolean doLogChange(Property<?> property) {
		EntityWithPropertiesBase entity = ((EntityWithPropertiesBase) property.getEntity());
		if (!doLogChange(entity)) {
			return false;
		}
		return entity.doLogChange(property.getName());
	}

	protected abstract boolean doLogChange(String propertyName);

	@Override
	public <T> void setValueByPath(String relativePath, T value) {
		if (this.isFrozen()) {
			throw new IllegalStateException("Cannot set value on a frozen entity.");
		}
		if (relativePath == null || relativePath.isEmpty()) {
			throw new IllegalArgumentException("Path cannot be null or empty");
		}
		Property<T> property = getPropertyByPath(relativePath);

		if (!(property instanceof BaseProperty)) {
			throw new IllegalArgumentException("Property '" + property.getName() + "' is not a simple property and cannot be set directly. Path: " + relativePath);
		}

		((BaseProperty<T>) property).setValue(value);
	}

	@Override
	public <T> Property<T> getPropertyByPath(String relativePath) {
		if (this.isFrozen()) {
			throw new IllegalStateException("Cannot get property by path on a frozen entity.");
		}
		if (relativePath == null || relativePath.isEmpty()) {
			throw new IllegalArgumentException("Path cannot be null or empty");
		}
		String[] pathSegments = relativePath.split("\\.");
		if (pathSegments.length == 0) {
			throw new IllegalArgumentException("Path is empty after splitting: " + relativePath);
		}
		return findPropertyRecursive(this, pathSegments, 0, relativePath);
	}

	@SuppressWarnings("unchecked")
	private <T> Property<T> findPropertyRecursive(EntityWithProperties currentEntity, String[] pathSegments, int currentIndex, String originalPath) {
		String segment = pathSegments[currentIndex];
		boolean isLastSegment = (currentIndex == pathSegments.length - 1);

		Matcher matcher = SEGMENT_PATTERN.matcher(segment);

		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid path segment format: '" + segment + "' in path '" + originalPath + "'");
		}

		String propName = matcher.group(1);
		String indexStr = matcher.group(2);

		Property<?> property = currentEntity.getProperty(propName);
		if (property == null) {
			throw new IllegalArgumentException("Property '" + propName + "' not found on entity " + currentEntity.getClass().getSimpleName() + " in path '" + originalPath + "'");
		}

		if (indexStr != null) {
			if (!(property instanceof PartListProperty<?> partListProp)) {
				throw new IllegalArgumentException("Property '" + propName + "' is not a list, but path specifies an index. Path: " + originalPath);
			}
			int index = Integer.parseInt(indexStr);

			if (index < 0 || index >= partListProp.getPartCount()) {
				throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list '" + propName + "' (size: " + partListProp.getPartCount() + ") in path '" + originalPath + "'");
			}

			EntityWithProperties nextEntity = partListProp.getPart(index);
			if (nextEntity == null) {
				throw new IllegalStateException("Part at index " + index + " in list '" + propName + "' is null. Path: " + originalPath);
			}

			if (isLastSegment) {
				throw new IllegalArgumentException("Path cannot end with a direct list access. Path: " + originalPath);
			}

			return findPropertyRecursive(nextEntity, pathSegments, currentIndex + 1, originalPath);
		}

		if (isLastSegment) {
			return (Property<T>) property;
		}

		String nextSegment = pathSegments[currentIndex + 1];

		if (currentIndex + 2 == pathSegments.length && "id".equals(nextSegment)) {
			if (property instanceof ReferenceProperty) {
				return (Property<T>) ((ReferenceProperty<?>) property).getIdProperty();
			} else if (property instanceof PartReferenceProperty) {
				return (Property<T>) ((PartReferenceProperty<?>) property).getIdProperty();
			}
		}

		if ((property instanceof ReferenceProperty || property instanceof PartReferenceProperty) && "id".equals(nextSegment)) {
			throw new IllegalArgumentException("Cannot navigate deeper from '.id' of a reference property. Path: " + originalPath);
		}

		EntityWithProperties nextEntity = getEntityWithProperties(originalPath, property, propName);
		return findPropertyRecursive(nextEntity, pathSegments, currentIndex + 1, originalPath);
	}

	@NotNull
	private static EntityWithProperties getEntityWithProperties(String originalPath, Property<?> property, String propName) {
		EntityWithProperties nextEntity;
		if (property instanceof PartReferenceProperty) {
			nextEntity = ((PartReferenceProperty<?>) property).getValue();
		} else if (property instanceof ReferenceProperty) {
			nextEntity = ((ReferenceProperty<?>) property).getValue();
		} else {
			throw new IllegalArgumentException("Property '" + propName + "' is not a container (PartList, PartReference, or Reference). Cannot navigate deeper in path '" + originalPath + "'");
		}

		if (nextEntity == null) {
			throw new IllegalStateException("Reference '" + propName + "' is null. Path: " + originalPath);
		}
		return nextEntity;
	}

}
