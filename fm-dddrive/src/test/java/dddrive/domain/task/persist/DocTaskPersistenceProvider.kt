package dddrive.domain.task.persist

import dddrive.ddd.core.model.AggregatePersistenceProvider
import dddrive.domain.task.model.DocTask

interface DocTaskPersistenceProvider : AggregatePersistenceProvider<DocTask>
