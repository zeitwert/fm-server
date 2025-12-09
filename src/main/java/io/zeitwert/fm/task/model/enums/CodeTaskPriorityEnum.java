
package io.zeitwert.fm.task.model.enums;

import jakarta.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.task.model.db.Tables;
import io.zeitwert.fm.task.model.db.tables.records.CodeTaskPriorityRecord;

@Component("codeTaskPriorityEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeTaskPriorityEnum extends JooqEnumerationBase<CodeTaskPriority> {

	private static CodeTaskPriorityEnum INSTANCE;

	protected CodeTaskPriorityEnum(AppContextSPI appContext, DSLContext dslContext) {
		super(CodeTaskPriority.class, appContext, dslContext);
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
