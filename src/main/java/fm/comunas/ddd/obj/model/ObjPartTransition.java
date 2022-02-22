package fm.comunas.ddd.obj.model;

import fm.comunas.ddd.oe.model.ObjUser;

import java.time.OffsetDateTime;

public interface ObjPartTransition extends ObjPart<Obj> {

	Integer getSeqNr();

	Integer getUserId();

	ObjUser getUser();

	OffsetDateTime getModifiedAt();

	String getChanges();

}
