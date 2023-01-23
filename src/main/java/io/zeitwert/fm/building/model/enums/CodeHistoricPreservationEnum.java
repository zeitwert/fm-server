
package io.zeitwert.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeHistoricPreservationRecord;

@Component("codeHistoricPreservationEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeHistoricPreservationEnum extends EnumerationBase<CodeHistoricPreservation> {

	private static CodeHistoricPreservationEnum INSTANCE;

	private CodeHistoricPreservationEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeHistoricPreservationRecord item : this.getDslContext().selectFrom(Tables.CODE_HISTORIC_PRESERVATION)
				.fetch()) {
			CodeHistoricPreservation historicPreservation = CodeHistoricPreservation.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(historicPreservation);
		}
	}

	public static CodeHistoricPreservation getHistoricPreservation(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
