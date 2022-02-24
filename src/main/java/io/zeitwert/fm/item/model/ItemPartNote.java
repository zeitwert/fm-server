package io.zeitwert.fm.item.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.item.model.ItemPart;
import io.zeitwert.ddd.oe.model.ObjUser;

import java.time.OffsetDateTime;

public interface ItemPartNote<A extends Aggregate> extends ItemPart<A> {

	String getSubject();

	void setSubject(String subject);

	String getContent();

	void setContent(String content);

	Boolean getIsPrivate();

	void setIsPrivate(Boolean isPrivate);

	OffsetDateTime getCreatedAt();

	ObjUser getCreatedByUser();

	OffsetDateTime getModifiedAt();

	ObjUser getModifiedByUser();

}
