package io.zeitwert.fm.task.config

import io.dddrive.core.ddd.model.enums.CodeAggregateType
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.core.enums.model.base.EnumConfigBase
import io.zeitwert.fm.task.model.enums.CodeTaskPriority
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component("taskConfig")
class TaskConfig : EnumConfigBase(), InitializingBean {

    @Autowired
    lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

    override fun afterPropertiesSet() {
        try {
            startConfig()
            aggregateTypeEnum.addItem(CodeAggregateType(aggregateTypeEnum, "doc_task", "Task"))

            CodeTaskPriority.entries
        } finally {
            endConfig()
        }
    }
}

