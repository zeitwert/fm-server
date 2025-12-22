package io.dddrive.domain.task.persist

import io.dddrive.ddd.model.AggregatePersistenceProvider
import io.dddrive.domain.task.model.DocTask

interface DocTaskPersistenceProvider : AggregatePersistenceProvider<DocTask>
