package io.dddrive.jooq.doc;

import java.time.OffsetDateTime;

import io.dddrive.doc.model.DocPartTransition;
import io.dddrive.jooq.ddd.AggregatePropertyProviderMixin;
import io.dddrive.jooq.ddd.AggregateState;

public interface DocPropertyProviderMixin
		extends AggregatePropertyProviderMixin {

	@Override
	default void mapProperties() {
		this.mapField("id", AggregateState.BASE, "id", Integer.class);
		this.mapField("docTypeId", AggregateState.BASE, "doc_type_id", String.class);
		this.mapField("tenant", AggregateState.BASE, "tenant_id", Integer.class);
		this.mapField("accountId", AggregateState.BASE, "account_id", Integer.class);
		this.mapField("owner", AggregateState.BASE, "owner_id", Integer.class);
		this.mapField("caption", AggregateState.BASE, "caption", String.class);
		this.mapField("version", AggregateState.BASE, "version", Integer.class);
		this.mapField("createdByUser", AggregateState.BASE, "created_by_user_id", Integer.class);
		this.mapField("createdAt", AggregateState.BASE, "created_at", OffsetDateTime.class);
		this.mapField("modifiedByUser", AggregateState.BASE, "modified_by_user_id", Integer.class);
		this.mapField("modifiedAt", AggregateState.BASE, "modified_at", OffsetDateTime.class);
		this.mapField("caseDef", AggregateState.BASE, "case_def_id", String.class);
		this.mapField("caseStage", AggregateState.BASE, "case_stage_id", String.class);
		this.mapField("isInWork", AggregateState.BASE, "is_in_work", Boolean.class);
		this.mapField("assignee", AggregateState.BASE, "assignee_id", Integer.class);
		this.mapCollection("transitionList", "doc.transitionList", DocPartTransition.class);
	}

}
