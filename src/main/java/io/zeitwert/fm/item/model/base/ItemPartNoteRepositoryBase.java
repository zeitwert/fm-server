package io.zeitwert.fm.item.model.base;

import org.jooq.DSLContext;
import org.jooq.Result;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.item.model.base.ItemPartRepositoryBase;
import io.zeitwert.fm.item.model.ItemPartNote;
import io.zeitwert.fm.item.model.ItemPartNoteRepository;
import io.zeitwert.fm.item.model.db.Tables;
import io.zeitwert.fm.item.model.db.tables.records.ItemPartNoteRecord;

import java.util.List;

public abstract class ItemPartNoteRepositoryBase<A extends Aggregate>
		extends ItemPartRepositoryBase<A, ItemPartNote<A>>
		implements ItemPartNoteRepository<A> {

	//@formatter:off
	protected ItemPartNoteRepositoryBase(
		final Class<? extends A> aggregateIntfClass,
		final Class<? extends ItemPartNote<A>> intfClass,
		final Class<? extends ItemPartNote<A>> baseClass,
		final String partTypeId,
		final AppContext appContext,
		final DSLContext dslContext
	) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId, appContext, dslContext);
	}
	//@formatter:on

	@Override
	public List<ItemPartNote<A>> doLoad(A item) {
		//@formatter:off
		Result<ItemPartNoteRecord> dbRecords = this.getDSLContext()
			.selectFrom(Tables.ITEM_PART_NOTE)
			.where(Tables.ITEM_PART_NOTE.ITEM_ID.eq(item.getId()))
			.orderBy(Tables.ITEM_PART_NOTE.SEQ_NR)
			.fetchInto(Tables.ITEM_PART_NOTE);
		//@formatter:on
		return dbRecords.map(dbRecord -> this.newPart(item, dbRecord));
	}

	@Override
	public ItemPartNote<A> doCreate(A item) {
		ItemPartNoteRecord dbRecord = this.getDSLContext().newRecord(Tables.ITEM_PART_NOTE);
		return this.newPart(item, dbRecord);
	}

}
