
package io.zeitwert.fm.task.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.task.model.db.Tables;
import io.zeitwert.fm.task.model.db.tables.records.CodeTaskPriorityRecord;

@Component("codeTaskPriorityEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeTaskPriorityEnum extends EnumerationBase<CodeTaskPriority> {

	private static CodeTaskPriorityEnum INSTANCE;

	protected CodeTaskPriorityEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeTaskPriorityRecord item : this.getDslContext().selectFrom(Tables.CODE_TASK_PRIORITY).fetch()) {
			this.addItem(new CodeTaskPriority(this, item.getId(), item.getName()));
		}
	}

	public static CodeTaskPriority getPriority(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
