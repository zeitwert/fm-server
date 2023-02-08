
package io.zeitwert.fm.task.model.enums;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.task.model.db.Tables;
import io.zeitwert.fm.task.model.db.tables.records.CodeTaskPriorityRecord;

@Component("codeTaskPriorityEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeTaskPriorityEnum extends EnumerationBase<CodeTaskPriority> {

	private static CodeTaskPriorityEnum INSTANCE;

	protected CodeTaskPriorityEnum(AppContext appContext) {
		super(appContext, CodeTaskPriority.class);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeTaskPriorityRecord item : this.getDslContext().selectFrom(Tables.CODE_TASK_PRIORITY).fetch()) {
			CodeTaskPriority taskPriority = CodeTaskPriority.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(taskPriority);
		}
	}

	public static CodeTaskPriority getPriority(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
