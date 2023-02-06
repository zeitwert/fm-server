package io.zeitwert.ddd.persistence.jooq.base;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregatePersistenceProvider;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.base.AggregateSPI;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.persistence.jooq.AggregateState;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.fm.search.model.db.Tables;

public abstract class AggregatePersistenceProviderBase<A extends Aggregate> extends PropertyProviderBase
		implements AggregatePersistenceProvider<A> {

	static public final String BASE = "base";
	static public final String EXTN = "extn";

	private final DSLContext dslContext;
	private final Class<? extends AggregateRepository<A, ?>> repoIntfClass;

	public AggregatePersistenceProviderBase(
			Class<? extends AggregateRepository<A, ?>> repoIntfClass,
			Class<? extends Aggregate> baseClass,
			DSLContext dslContext) {
		this.repoIntfClass = repoIntfClass;
		this.dslContext = dslContext;
	}

	protected final DSLContext getDSLContext() {
		return this.dslContext;
	}

	protected final AggregateRepository<A, ?> getRepository() {
		return AppContext.getInstance().getBean(this.repoIntfClass);
	}

	@Override
	protected UpdatableRecord<?> getDbRecord(EntityWithPropertiesSPI entity, String tableType) {
		Object state = ((AggregateSPI) entity).getAggregateState();
		if (EXTN.equals(tableType)) {
			return ((AggregateState) state).extnRecord();
		} else if (BASE.equals(tableType)) {
			return ((AggregateState) state).baseRecord();
		}
		return null;
	}

	@Override
	public final void storeSearch(Aggregate aggregate, List<String> texts, List<String> tokens) {
		String allTexts = String.join(" ", texts.stream().filter(t -> t != null).toList()).toLowerCase();
		String allTokens = String.join(" ", tokens.stream().filter(t -> t != null).toList()).toLowerCase();
		String allTextsAndTokens = (allTexts + " " + allTokens).trim();
		String id = aggregate.getMeta().getAggregateType().getId() + ":" +
				aggregate.getId();
		this.dslContext
				.delete(Tables.ITEM_SEARCH)
				.where(Tables.ITEM_SEARCH.ID.eq(id))
				.execute();
		this.dslContext
				.insertInto(
						Tables.ITEM_SEARCH,
						Tables.ITEM_SEARCH.ID,
						Tables.ITEM_SEARCH.ITEM_TYPE_ID,
						Tables.ITEM_SEARCH.ITEM_ID,
						Tables.ITEM_SEARCH.A_SIMPLE,
						Tables.ITEM_SEARCH.B_GERMAN,
						Tables.ITEM_SEARCH.B_ENGLISH)
				.values(
						id,
						aggregate.getMeta().getAggregateType().getId(),
						aggregate.getId(),
						allTokens,
						allTextsAndTokens,
						allTextsAndTokens)
				.execute();
	}

	@Override
	public Result<?> doQuery(Table<? extends Record> table, Condition whereClause, List<SortField<?>> sortFields,
			Long offset,
			Long limit) {
		return this.dslContext
				.select()
				.from(table)
				.where(whereClause)
				.orderBy(sortFields)
				.limit(offset, limit)
				.fetch();
	}

}
