
package io.zeitwert.ddd.enums.model.base;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.zeitwert.ddd.util.Check.assertThis;

public abstract class EnumerationBase<E extends Enumerated> implements Enumeration<E> {

	private final String module;
	private final String id;
	private List<E> items = new ArrayList<E>();
	private Map<String, E> itemsById = new HashMap<String, E>();

	public EnumerationBase(Class<E> enumeratedClass, Enumerations enums) {
		String[] parts = this.getClass().getCanonicalName().split("\\.");
		assertThis(parts.length == 7, "valid enumeration class name (io.zeitwert.[area].[module].model.enums.[xyEnum])");
		assertThis("model".equals(parts[4]),
				"valid enumeration class name (io.zeitwert.[area].[module].model.enums.[xyEnum])");
		assertThis("enums".equals(parts[5]),
				"valid enumeration class name (io.zeitwert.[area].[module].model.enums.[xyEnum])");
		assertThis(parts[6].endsWith("Enum"), "valid enumeration class name");
		this.module = parts[3];
		this.id = Character.toLowerCase(parts[6].charAt(0)) + parts[6].substring(1);
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
