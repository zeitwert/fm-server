package io.dddrive.core.enums.model.base;

import static io.dddrive.util.Invariant.assertThis;
import static io.dddrive.util.Invariant.requireThis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dddrive.core.ddd.model.RepositoryDirectory;
import io.dddrive.core.ddd.model.RepositoryDirectorySPI;
import io.dddrive.core.enums.model.Enumerated;
import io.dddrive.core.enums.model.Enumeration;

public abstract class EnumerationBase<E extends Enumerated> implements Enumeration<E> {

	private final String area;
	private final String module;
	private final String id;
	private final List<E> items = new ArrayList<E>();
	private final Map<String, E> itemsById = new HashMap<String, E>();

	public EnumerationBase(Class<E> enumeratedClass) {
		String[] parts = enumeratedClass.getCanonicalName().split("\\.");
		int numOfParts = parts.length;

		assertThis(numOfParts > 5,
				() -> "valid enumeration class name (i), ([company/project].[area].[module].model.enums.[xyEnum]): "
						+ this.getClass().getCanonicalName());
		assertThis("model".equals(parts[numOfParts - 3]),
				() -> "valid enumeration class name (ii), must end with (model.enums.[xyEnum]): "
						+ this.getClass().getCanonicalName());
		assertThis("enums".equals(parts[numOfParts - 2]),
				() -> "valid enumeration class name (iii), must end with (model.enums.[xyEnum]): "
						+ this.getClass().getCanonicalName());

		this.area = parts[numOfParts - 5];
		this.module = parts[numOfParts - 4];
		this.id = Character.toLowerCase(parts[numOfParts - 1].charAt(0)) + parts[numOfParts - 1].substring(1) + "Enum";

		((RepositoryDirectorySPI) RepositoryDirectory.getInstance()).addEnumeration(enumeratedClass, this);
	}

	@Override
	public String getArea() {
		return this.area;
	}

	@Override
	public String getModule() {
		return this.module;
	}

	@Override
	public String getId() {
		return this.id;
	}

	public void addItem(E item) {
		requireThis(EnumConfigBase.isInConfig(), () -> "in ddd configuration");
		requireThis(item != null, () -> "valid item");
		requireThis(this.itemsById.get(item.getId()) == null, () -> "unique item [" + item.getId() + "] in enumeration [" + this.getId() + "]");
		EnumConfigBase.addEnum(this);
		this.items.add(item);
		this.itemsById.put(item.getId(), item);
	}

	public void assignItems() {
		requireThis(EnumConfigBase.isInConfig(), () -> "in ddd configuration");
	}

	@Override
	public List<E> getItems() {
		return this.items;
	}

	@Override
	public E getItem(String id) {
		if (id == null) {
			return null;
		}
		E item = this.itemsById.get(id);
		assertThis(item != null, () -> "valid item [" + id + "] in enumeration [" + this.getId() + "]");
		return item;
	}

	@Override
	public String getResourcePath() {
		return this.module + "." + this.id.replace("Enum", "");
	}

}
