package io.dddrive.core.oe.model;

import io.dddrive.core.obj.model.Obj;

public interface ObjTenant extends Obj {

	String getKey();

	void setKey(String key);

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

}
