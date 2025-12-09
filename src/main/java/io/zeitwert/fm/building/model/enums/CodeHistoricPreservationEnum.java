
package io.zeitwert.fm.building.model.enums;

import jakarta.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeHistoricPreservationRecord;

@Component("codeHistoricPreservationEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeHistoricPreservationEnum extends JooqEnumerationBase<CodeHistoricPreservation> {

	private static CodeHistoricPreservationEnum INSTANCE;

	private CodeHistoricPreservationEnum(AppContextSPI appContext, DSLContext dslContext) {
		super(CodeHistoricPreservation.class, appContext, dslContext);
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
