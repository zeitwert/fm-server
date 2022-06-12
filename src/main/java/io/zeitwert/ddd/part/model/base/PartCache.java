
package io.zeitwert.ddd.part.model.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.zeitwert.ddd.util.Check.assertThis;
import static io.zeitwert.ddd.util.Check.requireThis;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.part.model.Part;

public class PartCache<A extends Aggregate, P extends Part<A>> {

	private final Map<A, List<P>> cache = new ConcurrentHashMap<>();

	public boolean isInitialised(A aggregate) {
		requireThis(aggregate != null, "aggregate not null");
		return this.cache.containsKey(aggregate);
	}

	public void initParts(A aggregate) {
		requireThis(aggregate != null, "aggregate not null");
		requireThis(!this.isInitialised(aggregate), this.getClass().getName() + ": aggregate not yet initialised");
		this.cache.put(aggregate, new ArrayList<>());
		assertThis(this.isInitialised(aggregate), this.getClass().getName() + ": aggregate initialised");
	}

	public List<P> getParts(A aggregate) {
		requireThis(aggregate != null, "aggregate not null");
		requireThis(this.isInitialised(aggregate), this.getClass().getSimpleName() + ": aggregate initialised");
		return this.cache.get(aggregate);
	}

	public void clearParts(A aggregate) {
		this.cache.remove(aggregate);
	}

	public void addPart(P part) {
		A aggregate = part.getMeta().getAggregate();
		requireThis(this.isInitialised(aggregate),
				this.getClass().getSimpleName() + ": aggregate " + aggregate.getId() + " initialised");
		this.cache.get(aggregate).add(part);
	}

}
