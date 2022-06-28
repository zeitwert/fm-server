
package io.zeitwert.ddd.doc.model;

import java.util.List;

import io.zeitwert.ddd.aggregate.model.AggregateMeta;

public interface DocMeta extends AggregateMeta {

	List<DocPartTransition> getTransitionList();

}
