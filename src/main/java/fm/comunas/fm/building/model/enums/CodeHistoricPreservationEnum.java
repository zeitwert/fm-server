
package fm.comunas.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.enums.model.base.EnumerationBase;
import fm.comunas.fm.building.model.db.Tables;
import fm.comunas.fm.building.model.db.tables.records.CodeHistoricPreservationRecord;

@Component("codeHistoricPreservationEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeHistoricPreservationEnum extends EnumerationBase<CodeHistoricPreservation> {

	private static CodeHistoricPreservationEnum INSTANCE;

	@Autowired
	private CodeHistoricPreservationEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeHistoricPreservationRecord item : this.getDslContext().selectFrom(Tables.CODE_HISTORIC_PRESERVATION)
				.fetch()) {
			this.addItem(new CodeHistoricPreservation(this, item.getId(), item.getName()));
		}
	}

	public static CodeHistoricPreservation getHistoricPreservation(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
