
package io.dddrive.enums.model.base;

import io.dddrive.app.service.api.impl.Enumerations;
import io.dddrive.enums.model.Enumerated;
import io.dddrive.enums.model.Enumeration;

import static io.dddrive.util.Invariant.assertThis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EnumerationBase<E extends Enumerated> implements Enumeration<E> {

	private final String module;
	private final String id;
	private List<E> items = new ArrayList<E>();
	private Map<String, E> itemsById = new HashMap<String, E>();

	public EnumerationBase(Class<E> enumeratedClass, Enumerations enums) {
		String[] parts = this.getClass().getCanonicalName().split("\\.");
		int numOfParts = parts.length;
		// assertThis(parts.length == 7,
		// () -> "valid enumeration class name i
		// ([io].[company/project].[area].[module].model.enums.[xyEnum]): "
		// + this.getClass().getCanonicalName());
		assertThis(numOfParts >= 4,
				() -> "valid enumeration class name (i), ([company/project].[module].model.enums.[xyEnum]): "
						+ this.getClass().getCanonicalName());
		assertThis("model".equals(parts[numOfParts - 3]),
				() -> "valid enumeration class name (ii), must end with (model.enums.[xyEnum]): "
						+ this.getClass().getCanonicalName());
		assertThis("enums".equals(parts[numOfParts - 2]),
				() -> "valid enumeration class name (iii), must end with (model.enums.[xyEnum]): "
						+ this.getClass().getCanonicalName());
		assertThis(parts[numOfParts - 1].endsWith("Enum"),
				() -> "valid enumeration class name (iv), must end with (model.enums.[xyEnum]): "
						+ this.getClass().getCanonicalName());
		this.module = parts[numOfParts - 4];
		this.id = Character.toLowerCase(parts[numOfParts - 1].charAt(0)) + parts[numOfParts - 1].substring(1);
		enums.addEnumeration(enumeratedClass, this);
	}

	@Override
	public String getModule() {
		return this.module;
	}

	@Override
	public String getId() {
		return this.id;
	}

	protected void addItem(E item) {
		this.items.add(item);
		this.itemsById.put(item.getId(), item);
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
		assertThis(item != null, "valid item [" + id + "]");
		return item;
	}

	@Override
	public String getResourcePath() {
		return this.module + "/" + this.id.replace("Enum", "");
	}

}
