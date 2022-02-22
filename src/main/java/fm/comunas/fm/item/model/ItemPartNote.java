package fm.comunas.fm.item.model;

import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.item.model.ItemPart;
import fm.comunas.ddd.oe.model.ObjUser;

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
