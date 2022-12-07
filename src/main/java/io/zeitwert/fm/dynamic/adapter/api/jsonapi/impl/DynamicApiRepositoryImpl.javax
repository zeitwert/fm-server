
package io.zeitwert.fm.dynamic.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ReadOnlyResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import io.zeitwert.fm.dynamic.model.Dynamic;

@Controller("dynamicApiRepository")
public class DynamicApiRepositoryImpl extends ReadOnlyResourceRepositoryBase<Dynamic, Integer> {

	public DynamicApiRepositoryImpl() {
		super(Dynamic.class);
	}

	@Override
	public ResourceList<Dynamic> findAll(QuerySpec querySpec) {
		ResourceList<Dynamic> list = new DefaultResourceList<Dynamic>();
		list.add(new Dynamic());
		list.add(new Dynamic());
		list.add(new Dynamic());
		list.add(new Dynamic());
		// list.addAll(itemList.stream().map(item -> (DocAdviceImpl)
		// item).toList());
		return list;
	}

}
