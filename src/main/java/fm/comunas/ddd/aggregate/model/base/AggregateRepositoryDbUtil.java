
package fm.comunas.ddd.aggregate.model.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;

import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.enums.model.Enumerated;
import fm.comunas.ddd.enums.model.Enumeration;

public abstract class AggregateRepositoryDbUtil {

	protected abstract Table<?> getPartListTable();

	protected abstract Field<Integer> getPartListAggregateId();

	private static final Field<String> PART_LIST_TYPE_ID = DSL.field("part_list_type_id", String.class);
	private static final Field<Integer> SEQ_NR = DSL.field("seq_nr", Integer.class);
	private static final Field<String> ITEM_ID = DSL.field("item_id", String.class);

	// [aggregate_id], part_list_type_id, seq_nr, item_id

	@FunctionalInterface
	public static interface AggregateLookup<T> {
		T getItem(String itemId);
	}

	@FunctionalInterface
	public static interface IdProvider<T> {
		String getId(T item);
	}

	//@formatter:off
	private Result<Record1<String>> loadItemParts(DSLContext dslContext, Integer aggregateId, String partListTypeId) {
		return dslContext
			.select(ITEM_ID)
			.from(this.getPartListTable())
			.where(this.getPartListAggregateId().eq(aggregateId))
			.and(PART_LIST_TYPE_ID.eq(partListTypeId))
			.orderBy(SEQ_NR)
			.fetch();
	}
	//@formatter:on

	//@formatter:off
	private void deleteItemParts(DSLContext dslContext, Integer aggregateId, String partListTypeId) {
		dslContext
			.delete(this.getPartListTable())
			.where(this.getPartListAggregateId().eq(aggregateId))
			.and(PART_LIST_TYPE_ID.eq(partListTypeId))
			.execute();
	}
	//@formatter:on

	//@formatter:off
	private <T> void storeItemParts(DSLContext dslContext, Integer aggregateId, String partListTypeId, Collection<T> items, IdProvider<T> idProvider) {
		Integer seqNr = 0;
		for (final T item : items) {
			seqNr += 1;
			dslContext
				.insertInto(this.getPartListTable())
				.columns(
					this.getPartListAggregateId(),
					PART_LIST_TYPE_ID,
					SEQ_NR,
					ITEM_ID
				)
				.values(
					aggregateId,
					partListTypeId,
					seqNr,
					idProvider.getId(item)
				)
				.execute();
		}
	}
	//@formatter:on

	public <E extends Enumerated> Set<E> loadEnumSet(DSLContext dslContext, Integer aggregateId, String partListTypeId,
			Class<? extends Enumeration<E>> enumClass) {
		Enumeration<E> e = AppContext.getInstance().getEnumeration(enumClass);
		Set<E> listItems = new HashSet<>();
		for (final Record1<String> itemRecord : this.loadItemParts(dslContext, aggregateId, partListTypeId)) {
			listItems.add(e.getItem(itemRecord.value1()));
		}
		return listItems;
	}

	public void deleteEnumSet(DSLContext dslContext, Integer aggregateId, String partListTypeId) {
		deleteItemParts(dslContext, aggregateId, partListTypeId);
	}

	public void storeEnumSet(DSLContext dslContext, Integer aggregateId, String partListTypeId,
			Set<? extends Enumerated> enums) {
		storeItemParts(dslContext, aggregateId, partListTypeId, enums, (e) -> e.getId());
	}

	public <E extends Enumerated> List<E> loadEnumList(DSLContext dslContext, Integer aggregateId, String partListTypeId,
			Class<? extends Enumeration<E>> enumClass) {
		Enumeration<E> e = AppContext.getInstance().getEnumeration(enumClass);
		List<E> listItems = new ArrayList<>();
		for (final Record1<String> itemRecord : this.loadItemParts(dslContext, aggregateId, partListTypeId)) {
			listItems.add(e.getItem(itemRecord.value1()));
		}
		return listItems;
	}

	public void deleteEnumList(DSLContext dslContext, Integer aggregateId, String partListTypeId) {
		deleteItemParts(dslContext, aggregateId, partListTypeId);
	}

	public void storeEnumList(DSLContext dslContext, Integer aggregateId, String partListTypeId,
			List<? extends Enumerated> enums) {
		this.storeItemParts(dslContext, aggregateId, partListTypeId, enums, (e) -> e.getId());
	}

	public <T> Set<T> loadItemSet(DSLContext dslContext, Integer aggregateId, String partListTypeId,
			AggregateLookup<T> lookup) {
		Result<Record1<String>> itemRecords = this.loadItemParts(dslContext, aggregateId, partListTypeId);
		Set<T> items = new HashSet<>();
		for (final Record1<String> itemRecord : itemRecords) {
			items.add(lookup.getItem(itemRecord.value1()));
		}
		return items;
	}

	public void deleteItemSet(DSLContext dslContext, Integer aggregateId, String partListTypeId) {
		deleteItemParts(dslContext, aggregateId, partListTypeId);
	}

	public <T> void storeItemSet(DSLContext dslContext, Integer aggregateId, String partListTypeId, Set<T> items,
			IdProvider<T> idProvider) {
		this.storeItemParts(dslContext, aggregateId, partListTypeId, items, idProvider);
	}

	public <T> List<T> loadItemList(DSLContext dslContext, Integer aggregateId, String partListTypeId,
			AggregateLookup<T> lookup) {
		Result<Record1<String>> itemRecords = this.loadItemParts(dslContext, aggregateId, partListTypeId);
		List<T> items = new ArrayList<>();
		for (final Record1<String> itemRecord : itemRecords) {
			items.add(lookup.getItem(itemRecord.value1()));
		}
		return items;
	}

	public void deleteItemList(DSLContext dslContext, Integer aggregateId, String partListTypeId) {
		deleteItemParts(dslContext, aggregateId, partListTypeId);
	}

	public <T> void storeItemList(DSLContext dslContext, Integer aggregateId, String partListTypeId, List<T> items,
			IdProvider<T> idProvider) {
		this.storeItemParts(dslContext, aggregateId, partListTypeId, items, idProvider);
	}

}
