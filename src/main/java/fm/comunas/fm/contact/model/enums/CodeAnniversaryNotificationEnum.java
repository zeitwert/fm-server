
package fm.comunas.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import fm.comunas.fm.contact.model.db.tables.records.CodeAnniversaryNotificationRecord;
import fm.comunas.fm.contact.model.db.Tables;
import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.enums.model.base.EnumerationBase;

@Component("codeAnniversaryNotificationEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAnniversaryNotificationEnum extends EnumerationBase<CodeAnniversaryNotification> {

	private static CodeAnniversaryNotificationEnum INSTANCE;

	@Autowired
	protected CodeAnniversaryNotificationEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeAnniversaryNotificationRecord item : this.getDslContext()
				.selectFrom(Tables.CODE_ANNIVERSARY_NOTIFICATION).fetch()) {
			this.addItem(new CodeAnniversaryNotification(this, item.getId(), item.getName()));
		}
	}

	public static CodeAnniversaryNotification getAnniversaryNotification(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
