
package io.zeitwert.ddd.part.model.base;

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

public abstract class PartRepositoryDbUtil {

	protected abstract Table<?> getPartListTable();

	protected abstract Field<Integer> getPartListAggregateId();

	private static final Field<Integer> PARENT_PART_ID = DSL.field("parent_part_id", Integer.class);
	private static final Field<String> PART_LIST_TYPE_ID = DSL.field("part_list_type_id", String.class);
	private static final Field<Integer> SEQ_NR = DSL.field("seq_nr", Integer.class);
	private static final Field<String> ITEM_ID = DSL.field("item_id", String.class);

	// [aggregate_id], parent_part_id, part_list_type_id, seq_nr, item_id

	@FunctionalInterface
	public static interface PartLookup<T> {
		T getPart(String itemId);
	}

	@FunctionalInterface
	public static interface IdProvider<T> {
		String getId(T item);
	}

	//@formatter:off
	private Result<Record1<String>> loadItemParts(DSLContext dslContext, Integer aggregateId, Integer parentId, String partListTypeId) {
		return dslContext
			.select(ITEM_ID)
			.from(this.getPartListTable())
			.where(this.getPartListAggregateId().eq(aggregateId))
			.and(PARENT_PART_ID.eq(parentId))
			.and(PART_LIST_TYPE_ID.eq(partListTypeId))
			.orderBy(SEQ_NR)
			.fetch();
	}
	//@formatter:on

	//@formatter:off
	private <T> void storeItemParts(DSLContext dslContext, Integer aggregateId, Integer parentId, String partListTypeId, Collection<T> items, IdProvider<T> idProvider) {
		Integer seqNr = 0;
		for (final T item : items) {
			seqNr += 1;
			dslContext
				.insertInto(this.getPartListTable())
				.columns(
					this.getPartListAggregateId(),
					PARENT_PART_ID,
					PART_LIST_TYPE_ID,
					SEQ_NR,
					ITEM_ID
				)
				.values(
					aggregateId,
					parentId,
					partListTypeId,
					seqNr,
					idProvider.getId(item)
				)
				.execute();
		}
	}
	//@formatter:on

	public <T> Set<T> loadItemSet(DSLContext dslContext, Integer aggregateId, Integer parentId, String partListTypeId,
			PartLookup<T> lookup) {
		Result<Record1<String>> itemRecords = this.loadItemParts(dslContext, aggregateId, parentId, partListTypeId);
		Set<T> items = new HashSet<>();
		for (final Record1<String> itemRecord : itemRecords) {
			items.add(lookup.getPart(itemRecord.value1()));
		}
		return items;
	}

	public <T> void storeItemSet(DSLContext dslContext, Integer aggregateId, Integer parentId, String partListTypeId,
			Set<T> items, IdProvider<T> idProvider) {
		this.storeItemParts(dslContext, aggregateId, parentId, partListTypeId, items, idProvider);
	}

	public <T> List<T> loadItemList(DSLContext dslContext, Integer aggregateId, Integer parentId, String partListTypeId,
			PartLookup<T> lookup) {
		Result<Record1<String>> itemRecords = this.loadItemParts(dslContext, aggregateId, parentId, partListTypeId);
		List<T> items = new ArrayList<>();
		for (final Record1<String> itemRecord : itemRecords) {
			items.add(lookup.getPart(itemRecord.value1()));
		}
		return items;
	}

	public <T> void storeItemList(DSLContext dslContext, Integer aggregateId, Integer parentId, String partListTypeId,
			List<T> items, IdProvider<T> idProvider) {
		this.storeItemParts(dslContext, aggregateId, parentId, partListTypeId, items, idProvider);
	}

}
