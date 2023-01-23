
package io.zeitwert.fm.task.service.api.impl;

import org.springframework.stereotype.Service;

import io.zeitwert.ddd.aggregate.service.api.base.AggregateCacheBase;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.service.api.DocTaskCache;

@Service("taskCache")
public class DocTaskCacheImpl extends AggregateCacheBase<DocTask> implements DocTaskCache {

	public DocTaskCacheImpl(DocTaskRepository repository) {
		super(repository, DocTask.class);
	}

}
