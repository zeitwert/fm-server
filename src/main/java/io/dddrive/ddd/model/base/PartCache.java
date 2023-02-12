
package io.dddrive.ddd.model.base;

import java.util.ArrayList;
import java.util.List;

import io.dddrive.ddd.model.Part;

public class PartCache<P extends Part<?>> {

	private final List<P> cache = new ArrayList<>();

	public List<P> getParts() {
		return List.copyOf(this.cache);
	}

	public void clearParts() {
		this.cache.clear();
	}

	public void addPart(P part) {
		this.cache.add(part);
	}

}
