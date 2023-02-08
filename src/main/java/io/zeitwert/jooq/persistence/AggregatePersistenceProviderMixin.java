package io.zeitwert.jooq.persistence;

import java.util.List;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregatePersistenceProvider;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.fm.search.model.db.Tables;

public interface AggregatePersistenceProviderMixin<A extends Aggregate> extends AggregatePersistenceProvider<A> {

	DSLContext dslContext();

	AggregateRepository<A, ?> getRepository();

	@Override
	default void doStoreSearch(Aggregate aggregate, List<String> texts, List<String> tokens) {
		String allTexts = String.join(" ", texts.stream().filter(t -> t != null).toList()).toLowerCase();
		String allTokens = String.join(" ", tokens.stream().filter(t -> t != null).toList()).toLowerCase();
		String allTextsAndTokens = (allTexts + " " + allTokens).trim();
		String id = aggregate.getMeta().getAggregateType().getId() + ":" +
				aggregate.getId();
		this.dslContext()
				.delete(Tables.ITEM_SEARCH)
				.where(Tables.ITEM_SEARCH.ID.eq(id))
				.execute();
		this.dslContext()
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

}
